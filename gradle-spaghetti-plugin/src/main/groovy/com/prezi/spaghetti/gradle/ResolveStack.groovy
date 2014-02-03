package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle

import org.gradle.api.tasks.TaskAction
import org.gradle.api.artifacts.Configuration

class ResolveStack extends AbstractSpaghettiTask {

	public ResolveStack() {
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

		def stackTrace = StackTrace.parse(stackFile.text);

		// if (stackTrace.lines.size() == 0) {
		// 	throw new RuntimeException("Could not extract enough info from stack trace. Is it malformed?");
		// }

		println("Gathering bundles of configuration '" + configName + "' in " + project.name + "}");

		def bundleMap = gatherBundles(configName);

		println(bundleMap);
	}

	// bundlename -> bundle
	public Map<String, ModuleBundle> gatherBundles(String configName) {
		def files = project.getAllprojects().collect{
			it.configurations.findByName(configName).files;
		}.flatten();
		def bundles = ModuleDefinitionLookup.getAllBundles(files);
		return bundles.collectEntries {[it.name.fullyQualifiedName, it]};
	}

	// public String githubLink(String hash, ) {
	// }

}
