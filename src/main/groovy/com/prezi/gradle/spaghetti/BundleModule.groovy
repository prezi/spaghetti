package com.prezi.gradle.spaghetti

import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
/**
 * Created by lptr on 16/11/13.
 */
class BundleModule extends AbstractBundleTask {

	BundleModule()
	{
		this.inputFile = new File(project.buildDir, "module.js")
		this.outputFile = new File(project.buildDir, "spaghetti/module.zip")
	}

	@TaskAction
	bundle() {
		def config = readConfig(definition.text)
		def module = config.localModules.first()
		def generator = project.getPlugins().getPlugin(SpaghettiPlugin).getGeneratorForPlatform(platform)
		def processedJavaScript = generator.processModuleJavaScript(config, module, inputFile.text)

		def bundledJavaScript = ""
		bundledJavaScript += "define(["
		bundledJavaScript += config.dependentModules.collect { dependentModule -> "\"${dependentModule.name.localName}\"" }.join(",")
		bundledJavaScript += "], function() {\n"
		bundledJavaScript += "var __modules = arguments;\n"
		bundledJavaScript += processedJavaScript
		bundledJavaScript += "});\n"

		def bundle = new ModuleBundle(module.name, definition.text, bundledJavaScript)
		bundle.save(outputFile)
	}

	@Override
	@InputFile
	File getDefinition()
	{
		return super.getDefinition()
	}
}
