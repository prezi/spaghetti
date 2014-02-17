package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle
import com.prezi.spaghetti.Wrapper
import com.prezi.spaghetti.Wrapping
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 16/11/13.
 */
class BundleModule extends AbstractBundleTask {

	public final File jsModuleFile

	File sourceMap

	BundleModule()
	{
		this.jsModuleFile = new File(project.buildDir, "spaghetti/module.js")
		this.conventionMapping.inputFile = { new File(project.buildDir, "module.js") }
		this.conventionMapping.outputFile = { new File(project.buildDir, "spaghetti/module.zip") }
	}

	@TaskAction
	bundle() {
		def config = readConfig(getDefinition())
		def module = config.getLocalModules().first()
		def processedJavaScript = createGenerator(config).processModuleJavaScript(module, getInputFile().text)
		def wrappedJavaScript = Wrapper.wrap(config, Wrapping.module, processedJavaScript)

		// is a sourcemap present?
		def sourceMapText = getSourceMap() ? getSourceMap().text : null;

		jsModuleFile.parentFile.mkdirs()
		jsModuleFile.delete()
		jsModuleFile << wrappedJavaScript

		def bundle = new ModuleBundle(module.name, getDefinition().text, String.valueOf(project.version), getSourceBaseUrl(), wrappedJavaScript, sourceMapText)
		bundle.save(getOutputFile())
	}

	void sourceMap(Object sourceMap)
	{
		this.sourceMap = project.file(sourceMap)
	}

	@InputFile
	@Optional
	File getSourceMap()
	{
		if (!sourceMap) {
			def defSourceMap = new File(getInputFile().toString() + ".map")
			if (defSourceMap.exists()) {
				sourceMap = defSourceMap
			}
		}
		return sourceMap
	}

	@Override
	@InputFile
	File getDefinition()
	{
		return super.getDefinition()
	}
}
