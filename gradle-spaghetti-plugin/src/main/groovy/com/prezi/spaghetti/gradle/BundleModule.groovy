package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle
import com.prezi.spaghetti.Wrapper
import com.prezi.spaghetti.Wrapping
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
		def wrappedJavaScript = Wrapper.wrap(config, Wrapping.module, processedJavaScript)

		jsFile.parentFile.mkdirs()
		jsFile.delete()
		jsFile << wrappedJavaScript

		def bundle = new ModuleBundle(module.name, definition.text, String.valueOf(project.version), sourceBaseUrl, wrappedJavaScript)
		bundle.save(outputFile)
	}

	@Override
	@InputFile
	File getDefinition()
	{
		return super.getDefinition()
	}
}
