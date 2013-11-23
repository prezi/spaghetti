package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
/**
 * Created by lptr on 16/11/13.
 */
class BundleModule extends AbstractBundleTask {

	private final File jsFile

	BundleModule()
	{
		this.inputFile = new File(project.buildDir, "module.js")
		this.jsFile = new File(project.buildDir, "spaghetti/module.js")
		this.outputFile = new File(project.buildDir, "spaghetti/module.zip")
	}

	@TaskAction
	bundle() {
		def config = readConfig(definition.text)
		def module = config.localModules.first()
		def processedJavaScript = createGenerator(config).processModuleJavaScript(module, inputFile.text)

		def bundledJavaScript = ""
		bundledJavaScript += "define(["
		bundledJavaScript += config.dependentModules.collect { "\"${it.name.localName}\"" }.join(",")
		bundledJavaScript += "], function() {\n"
		bundledJavaScript += "var __modules = arguments;\n"
		bundledJavaScript += processedJavaScript
		bundledJavaScript += "});\n"

		jsFile.parentFile.mkdirs()
		jsFile.delete()
		jsFile << bundledJavaScript

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
