package com.prezi.gradle.spaghetti

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
/**
 * Created by lptr on 16/11/13.
 */
class BundleApplication extends DefaultTask {
	@InputFiles
	Configuration configuration

	@InputFile
	File inputFile

	@OutputFile
	File outputFile

	@TaskAction
	bundle() {
		def definitions = ModuleDefinitionLookup.getAllDefinitions(configuration)
		def config = ModuleConfigurationParser.parse(definitions)

		outputFile.delete()
		outputFile << "require(["
		outputFile << config.modules.values().collect { module -> "\"${module.name.localName}\"" }.join(",")
		outputFile << "], function() {\n"
		outputFile << "var __modules = arguments;\n"
		outputFile << inputFile.text
		outputFile << "});\n"
	}

	def inputFile(Object f) {
		this.inputFile = project.file(f)
	}

	def outputFile(Object f) {
		this.outputFile = project.file(f)
	}

	def configuration(Configuration configuration) {
		this.configuration = configuration
	}
}
