package com.prezi.gradle.spaghetti

import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 17/11/13.
 */
class ExtractModules extends AbstractGenerateTask {
	ExtractModules() {
		this.outputDirectory = new File(project.buildDir, "spaghetti/modules")
	}

	@TaskAction
	extract() {
		outputDirectory.mkdirs()
		def bundles = ModuleDefinitionLookup.getAllBundles(configuration)
		bundles.each { bundle ->
			def outputFile = new File(outputDirectory, bundle.name.localName + ".js")
			outputFile.delete()
			outputFile << bundle.bundledJavaScript
		}
	}
}
