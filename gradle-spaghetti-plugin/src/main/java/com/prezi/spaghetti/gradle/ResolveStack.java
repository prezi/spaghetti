package com.prezi.spaghetti.gradle;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.debugging.sourcemap.SourceMapParseException;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.gradle.internal.AbstractSpaghettiTask;
import com.prezi.spaghetti.gradle.internal.StackTrace;
import com.prezi.spaghetti.gradle.internal.TextFileUtils;
import com.prezi.spaghetti.obfuscation.internal.SourceMap;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.internal.tasks.options.Option;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("UnusedDeclaration")
public class ResolveStack extends AbstractSpaghettiTask {
	private static final Pattern GIT_VERSION_HASH_PATTERN = Pattern.compile(".*-g([a-f0-9]{7})");

	@Option(option = "configuration", description = "The configuration to generate the report for.")
	@SuppressWarnings("UnusedDeclaration")
	public void setConfiguration(String configuration) {
		this.setDependentModules(getProject().getConfigurations().getByName(configuration));
	}

	@Override
	public Configuration getDependentModules() {
		Configuration modules = super.getDependentModules();
		return modules != null ? modules : getProject().getConfigurations().getByName("modulesObf");
	}

	@TaskAction
	public void run() throws IOException, SourceMapParseException {
		File stackFile;
		if (getProject().hasProperty("file")) {
			stackFile = getProject().file(getProject().property("file"));
		} else {
			throw new RuntimeException("Please give a stack trace file with -Pfile={filename here}");
		}

		boolean all = false;
		if (getProject().hasProperty("all")) {
			all = true;
		}

		StackTrace stackTrace = StackTrace.parse(TextFileUtils.getText(stackFile));

		if (stackTrace.getLines().isEmpty()) {
			throw new RuntimeException("Could not extract enough info from stack trace. Is it malformed?");
		}

		Map<String, ModuleBundle> bundleMap = gatherBundles();

		List<Resolution> resolvedLinks = Lists.newArrayList();
		for (StackTrace.StackTraceLine stackTraceLine : stackTrace.getLines()) {
			Resolution resolution;
			ModuleBundle bundle = bundleMap.get(stackTraceLine.jsName);
			if (bundle == null) {
				resolution = new Resolution(stackTraceLine.jsName, stackTraceLine.lineNo);
			} else if (bundle.getSourceMap() == null) {
				resolution = new Resolution(stackTraceLine.jsName, stackTraceLine.lineNo, bundle.getName(), bundle.getVersion());
			} else {
				StringBuilder sourceNameBuilder = new StringBuilder();
				int lineNo = SourceMap.reverseMapping(bundle.getSourceMap(), stackTraceLine.lineNo, sourceNameBuilder);
				String sourceName = sourceNameBuilder.toString();
				resolution = new Resolution(
						stackTraceLine.jsName,
						stackTraceLine.lineNo,
						bundle.getName(),
						bundle.getVersion(),
						sourceName,
						githubLink(bundle.getVersion(), bundle.getSourceBaseUrl(), sourceName, lineNo),
						lineNo);
			}

			resolvedLinks.add(resolution);
		}

		if (resolvedLinks.isEmpty()) {
			System.out.println("Could not resolve anything /sadface");
		} else {
			if (all) {
				for (Resolution link : resolvedLinks) {
					if (link.resolvedLink != null) {
						System.out.println(link.resolvedLink);
					}
				}
			} else {
				System.out.println(resolvedLinks.get(0).resolvedLink);
			}
		}
	}

	private Map<String, ModuleBundle> gatherBundles() throws IOException {
		return Maps.uniqueIndex(lookupBundles().getAllBundles(), new Function<ModuleBundle, String>() {
			@Override
			public String apply(ModuleBundle bundle) {
				return bundle.getName();
			}
		});
	}

	private static String versionToHash(String version) {
		Matcher matcher = GIT_VERSION_HASH_PATTERN.matcher(version);
		if (matcher.matches()) {
			return matcher.group(1);
		} else {
			return null;
		}
	}

	private static String githubLink(final String bundleVersion, final String githubRoot, final String relPath, final int lineNumber) {
		final String hash = versionToHash(bundleVersion);
		if (hash == null) {
			System.err.println("WARNING: bundle version " + bundleVersion + " is malformed/SNAPSHOT. Defaulting github link to master HEAD, may not work!");
			return githubRoot + "/blob/master/" + relPath + "#L" + lineNumber;
		} else {
			return githubRoot + "/blob/" + hash + "/" + relPath + "#L" + lineNumber;
		}
	}

	private static class Resolution {
		public final String origJsName;
		public final int origLineNo;
		public final String bundleName;
		public final String bundleVersion;
		public final boolean bundleHasSourceMap;
		public final String resolvedSourceName;
		public final String resolvedLink;
		public final int resolvedLineNo;

		public Resolution(String origJsName, int origLineNo) {
			this(origJsName, origLineNo, null, null);
		}

		public Resolution(String origJsName, int origLineNo, String bundleName, String bundleVersion) {
			this(origJsName, origLineNo, bundleName, bundleVersion, false, null, null, -1);
		}

		public Resolution(String origJsName, int origLineNo, String bundleName, String bundleVersion, String resolvedSourceName, String resolvedLink, int resolvedLineNo) {
			this(origJsName, origLineNo, bundleName, bundleVersion, true, resolvedSourceName, resolvedLink, resolvedLineNo);
		}

		private Resolution(String origJsName, int origLineNo, String bundleName, String bundleVersion, boolean bundleHasSourceMap, String resolvedSourceName, String resolvedLink, int resolvedLineNo) {
			this.origJsName = origJsName;
			this.origLineNo = origLineNo;
			this.bundleName = bundleName;
			this.bundleVersion = bundleVersion;
			this.bundleHasSourceMap = bundleHasSourceMap;
			this.resolvedSourceName = resolvedSourceName;
			this.resolvedLink = resolvedLink;
			this.resolvedLineNo = resolvedLineNo;
		}
	}
}
