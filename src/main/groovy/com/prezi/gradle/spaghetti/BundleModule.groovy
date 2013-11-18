package com.prezi.gradle.spaghetti

import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
/**
 * Created by lptr on 16/11/13.
 */
class BundleModule extends AbstractModuleTask {
	@InputFile
	File definition

	@InputFile
	File inputFile

	@OutputFile
	File outputFile

	String platform

	@TaskAction
	bundle() {
		def config = readConfig(definition.text)
		def module = config.localModules.first()
		def generator = project.getPlugins().getPlugin(SpaghettiPlugin).getGeneratorForPlatform(platform)
		def processedJavaScript = generator.processModuleJavaScript(config, module, inputFile.text)
		def bundle = new ModuleBundle(module.name, definition.text, processedJavaScript)
		bundle.save(outputFile)
	}

	def platform(String platform) {
		this.platform = platform
	}

	def definition(Object f) {
		this.definition = project.file(f)
	}

	def inputFile(Object f) {
		this.inputFile = project.file(f)
	}

	def outputFile(Object f) {
		this.outputFile = project.file(f)
	}

	@Override
	@InputFiles
	@Optional
	Configuration getConfiguration()
	{
		return super.getConfiguration()
	}
}
