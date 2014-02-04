package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle

import org.gradle.api.tasks.TaskAction
import org.gradle.api.artifacts.Configuration

class ResolveStack extends AbstractSpaghettiTask {

	public ResolveStack() {
	}

	private class Resolution {
		public String origJsName;
		public String toString() {return origJsName;}
		public String bundleName, bundleVersion, bundleHasSourceMap;
		public int origLineNo, resolvedLineNo, resolvedSourceName, resolvedLink;
	}

	@TaskAction
	void run() {
		def stackFile;
		if (project.hasProperty("file")) {
			stackFile = new File(project.file);
		} else {
						
			throw new RuntimeException("Please give a stack trace file with -Pfile={filename here}");
		}

		def configName = "modulesObf";
		if (project.hasProperty("config")) {
			configName = project.config;
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

		if (stackTrace.lines.size() == 0) {
			throw new RuntimeException("Could not extract enough info from stack trace. Is it malformed?");
		}

		// println("Doing magic, please stand by...")

		// println("Gathering bundles of configuration '" + configName + "' in " + project.name);

		def bundleMap = gatherBundles(configName);

		List<Resolution> resolvedLinks = stackTrace.lines.collect{
			if (it == null) {
				return null;
			}
			def bundle = bundleMap[it.jsName];
			if (bundle == null) {
				return [
					origJsName : it.jsName,
					origLineNo : it.lineNo];
			}
			if (bundle.sourceMap == null) {
				return [
					origJsName : it.jsName,
					origLineNo : it.lineNo,
					bundleName : bundle.name.fullyQualifiedName,
					bundleHasSourceMap : false,
					bundleVersion : bundle.version];
			} else {
				def sourceName = new StringBuilder();
				def lineNo = SourceMap.reverseMapping(bundle.sourceMap, it.lineNo, sourceName);

				return [
					origJsName : it.jsName,
					origLineNo : it.lineNo,
					bundleName : bundle.name.fullyQualifiedName,
					bundleHasSourceMap : true,
					bundleVersion : bundle.version,
					resolvedLineNo : lineNo,
					resolvedSourceName : sourceName.toString(),
					resolvedLink : githubLink(bundle.version, bundle.source,
											  sourceName.toString(), lineNo)];
			}
		};

		if (debug) {
			[stackFile.text.readLines(), resolvedLinks].transpose().each{
				println("\"${it[0]}\"");
				if (it[1] == null) {
					println("    Does not appear to be a valid stack trace line");
					// feels like PHP spirit
				} else {
					def msg = """    JS name:              ${it[1].origJsName}
    Line number:          ${it[1].origLineNo}""";
					if (it[1].bundleName == null) {
						msg += """
    Bundle name:          Could not find matching bundle""";
					} else {
						msg += """
    Bundle name:          ${it[1].bundleName}
    Bundle version:       ${it[1].bundleVersion}
    Sourcemap present:    ${it[1].bundleHasSourceMap ? "yes" : "no"}"""
						if (it[1].bundleHasSourceMap) {
							msg += """
    Resolved filename:    ${it[1].resolvedSourceName}
    Resolved line number: ${it[1].resolvedLineNo}
    Resolved github link: ${it[1].resolvedLink}"""
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
					println(links[0].resolvedLink);
				}
			} else {
				println("Could not resolve anything /sadface");
			}
		}
	}


	// bundlename -> bundle
	public Map<String, ModuleBundle> gatherBundles(String configName) {
		def files = project.configurations.findByName(configName).files;
		def bundles = ModuleDefinitionLookup.getAllBundles(files.collect{it});
		return bundles.collectEntries {[it.name.fullyQualifiedName, it]};
	}

	private static String versionToHash(String version) {
		def matcher = version =~ /-g([a-f0-9]{7})/;
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
