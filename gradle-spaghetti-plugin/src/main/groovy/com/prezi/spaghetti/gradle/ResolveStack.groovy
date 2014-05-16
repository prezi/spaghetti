package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.bundle.ModuleBundle
import com.prezi.spaghetti.SourceMap
import org.gradle.api.tasks.TaskAction

class ResolveStack extends AbstractSpaghettiTask {

	private static class Resolution {
		String bundleName
		String bundleVersion
		String bundleHasSourceMap;
		String origJsName;
		String resolvedSourceName
		String resolvedLink;
		int origLineNo
		int resolvedLineNo
	}

	@TaskAction
	void run() {
		def stackFile;
		if (project.hasProperty("file")) {
			stackFile = project.file(project.property("file"));
		} else {
			throw new RuntimeException("Please give a stack trace file with -Pfile={filename here}");
		}

		def configName = "modulesObf";
		if (project.hasProperty("config")) {
			configName = project.property("config") as String;
		}

		def debug = false;
		if (project.hasProperty("debug")) {
			debug = true;
		}

		def all = false;
		if (project.hasProperty("all")) {
			all = true;
		}

		def stackTrace = StackTrace.parse(stackFile.text);

		if (stackTrace.lines.empty) {
			throw new RuntimeException("Could not extract enough info from stack trace. Is it malformed?");
		}

		def bundleMap = gatherBundles(configName);

		List<Resolution> resolvedLinks = stackTrace.lines.collect{
			if (it == null) {
				return null;
			}
			def bundle = bundleMap[it.jsName];
			if (bundle == null) {
				return new Resolution(
					origJsName : it.jsName,
					origLineNo : it.lineNo
				)
			}
			if (bundle.sourceMap == null) {
				return new Resolution(
					origJsName : it.jsName,
					origLineNo : it.lineNo,
					bundleName : bundle.name,
					bundleHasSourceMap : false,
					bundleVersion : bundle.version
				)
			} else {
				def sourceName = new StringBuilder();
				def lineNo = SourceMap.reverseMapping(bundle.sourceMap, it.lineNo, sourceName);

				return new Resolution(
					origJsName : it.jsName,
					origLineNo : it.lineNo,
					bundleName : bundle.name,
					bundleHasSourceMap : true,
					bundleVersion : bundle.version,
					resolvedLineNo : lineNo,
					resolvedSourceName : sourceName.toString(),
					resolvedLink : githubLink(
							bundle.version, bundle.sourceBaseUrl,
							sourceName.toString(), lineNo
					)
				)
			}
		};

		if (debug) {
			[stackFile.text.readLines(), resolvedLinks].transpose().each{ line, resolution ->
				println("\"${line}\"");

				if (resolution == null) {
					println("    Does not appear to be a valid stack trace line");
				} else {
					def msg =
"""    JS name:              ${resolution.origJsName}
    Line number:          ${resolution.origLineNo}""";
					if (resolution.bundleName == null) {
						msg += """
    Bundle name:          Could not find matching bundle""";
					} else {
						msg += """
    Bundle name:          ${resolution.bundleName}
    Bundle version:       ${resolution.bundleVersion}
    Sourcemap present:    ${resolution.bundleHasSourceMap ? "yes" : "no"}"""
						if (resolution.bundleHasSourceMap) {
							msg += """
    Resolved filename:    ${resolution.resolvedSourceName}
    Resolved line number: ${resolution.resolvedLineNo}
    Resolved github link: ${resolution.resolvedLink}"""
						}
					}
					println(msg);
				}
			};
		} else {
			def links = resolvedLinks - null;
			if (links.size() > 0) {
				if (all) {
					links.each{if (it.resolvedLink != null) {println(it.resolvedLink)}};
				} else {
					println(links.first().resolvedLink);
				}
			} else {
				println("Could not resolve anything /sadface");
			}
		}
	}


	// bundlename -> bundle
	private Map<String, ModuleBundle> gatherBundles(String configName) {
		def configuration = project.configurations.getByName(configName)
		return ModuleDefinitionLookup.getAllBundles(configuration).collectEntries { bundle ->
			[ bundle.name, bundle ]
		}
	}

	private static String versionToHash(String version) {
		def matcher = version =~ /-g([a-f0-9]{7})$/;
		if (matcher.size() > 0 && matcher[0].size() > 1) {
			return matcher[0][1];
		} else {
			return null;
		}
	}

	private static String githubLink(String bundleVersion, String githubRoot, String relPath, int lineNumber) {
		def hash = versionToHash(bundleVersion);
		if (hash == null) {
			println("WARNING: bundle version ${bundleVersion} is malformed/SNAPSHOT. Defaulting github link to master HEAD, may not work!");
			return "${githubRoot}/blob/master/${relPath}#L${lineNumber}";
		} else {
			return "${githubRoot}/blob/${hash}/${relPath}#L${lineNumber}";
		}
	}

}
