package com.prezi.spaghetti.cli.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.prezi.spaghetti.bundle.ModuleBundleSet;
import com.prezi.spaghetti.cli.SpaghettiCliException;
import com.prezi.spaghetti.packaging.ApplicationPackageParameters;
import com.prezi.spaghetti.packaging.ApplicationType;
import com.prezi.spaghetti.structure.OutputType;
import io.airlift.command.Command;
import io.airlift.command.Option;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command(name = "package", description = "Package an application")
public class PackageApplicationCommand extends AbstractSpaghettiCommand {
	private static final Pattern EXTERNAL_LIB = Pattern.compile("([^:]+):(.+)");

	@Option(name = {"-T", "--type"},
			description = "Output type: zip or directory")
	private String type;

	@Option(name = {"--wrapper"},
			description = "Type of wrapper (AMD/RequireJS, NodeJS/CommonJS)",
			required = true)
	private String wrapper;

	@Option(name = {"-o", "--output"},
			description = "Output directory of ZIP file",
			required = true)
	private File output;

	@Option(name = {"--name"},
			description = "Application name (defaults to 'application')")
	private String name;

	@Option(name = {"--main"},
			description = "Main module")
	private String mainModule;

	@Option(name = {"--execute"},
			description = "Whether or not to auto-execute main()")
	private Boolean execute;

	@Option(name = {"-x, --external"},
			description = "Bind an external dependency to a concrete path, format: 'dependency:path'")
	private List<String> externalList = Lists.newArrayList();

	@Override
	public Integer call() throws Exception {
		OutputType type = OutputType.fromString(this.type, output);
		ApplicationType wrapper = ApplicationType.fromString(this.wrapper);

		ModuleBundleSet bundles = lookupBundles();

		// Transform list of externals to map from external name to path
		Map<String, String> externals = Maps.newLinkedHashMap();
		for (String colonPair : externalList) {
			Matcher matcher = EXTERNAL_LIB.matcher(colonPair);
			if (!matcher.matches()) {
				throw new SpaghettiCliException("Incorrect format for external dependency " + colonPair + ", use 'dependency:path'");
			}
			String name = matcher.group(1);
			String path = matcher.group(2);
			String previouslyConfiguredPath = externals.put(name, path);
			if (previouslyConfiguredPath != null) {
				logger.warn("More than one paths configured for external dependency '{}'", name);
			}
		}

		ApplicationPackageParameters params = new ApplicationPackageParameters(
				bundles,
				name != null ? name + ".js" : "application.js",
				mainModule,
				execute != null ? execute : mainModule != null,
				Collections.<String> emptySet(),
				Collections.<String> emptySet(),
				externals
				);

		switch (type) {
			case DIRECTORY:
				wrapper.getPackager().packageApplicationDirectory(output, params);
				break;
			case ZIP:
				wrapper.getPackager().packageApplicationZip(output, params);
				break;
		}
		return 0;
	}
}
