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

	public final File jsModuleFile

	BundleModule()
	{
		this.inputFile = new File(project.buildDir, "module.js")
		this.jsModuleFile = new File(project.buildDir, "spaghetti/module.js")
		this.outputFile = new File(project.buildDir, "spaghetti/module.zip")
	}

	@TaskAction
	bundle() {
		def config = readConfig(definition.text)
		def module = config.localModules.first()
		def processedJavaScript = createGenerator(config).processModuleJavaScript(module, inputFile.text)
		def wrappedJavaScript = Wrapper.wrap(config, Wrapping.module, processedJavaScript)

        // is a sourcemap present?
        def sourceMapFile = new File(inputFile.toString() + ".map");
        def sourceMap = sourceMapFile.canRead() ? sourceMapFile.text : null;

		jsModuleFile.parentFile.mkdirs()
		jsModuleFile.delete()
		jsModuleFile << wrappedJavaScript

		def bundle = new ModuleBundle(module.name, definition.text, wrappedJavaScript, sourceMap)
		bundle.save(outputFile)
	}

	@Override
	@InputFile
	File getDefinition()
	{
		return super.getDefinition()
	}
}
