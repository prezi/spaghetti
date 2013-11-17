package com.prezi.gradle.spaghetti

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 17/11/13.
 */
class ExtractModules extends DefaultTask {
	@InputFiles
	Configuration configuration

	@OutputDirectory
	File outputDirectory

	@TaskAction
	extract() {
		outputDirectory.mkdirs()
		def bundles = ModuleDefinitionLookup.getAllBundles(configuration)
		bundles.each { bundle ->
			def outputFile = new File(outputDirectory, bundle.name.localName + ".js")
			outputFile.delete()
			outputFile << bundle.compiledJavaScript
		}
	}

	void configuration(Configuration configuration) {
		this.configuration = configuration
	}

	void outputDirectory(Object dir) {
		this.outputDirectory = project.file(dir)
	}
}
