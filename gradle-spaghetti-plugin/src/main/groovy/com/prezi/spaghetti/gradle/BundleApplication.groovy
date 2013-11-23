package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleConfigurationParser
import org.gradle.api.tasks.TaskAction
/**
 * Created by lptr on 16/11/13.
 */
class BundleApplication extends AbstractBundleTask {

	BundleApplication()
	{
		this.inputFile = new File(project.buildDir, "application.js")
		this.outputFile = new File(project.buildDir, "spaghetti/application.js")
	}

	@TaskAction
	bundle() {
		def config = readConfig()
		def bundledJavaScript = createGenerator(config).processApplicationJavaScript(inputFile.text)

		outputFile.delete()
		outputFile << "require(["
		outputFile << config.modules.values().collect { "\"${it.name.localName}\"" }.join(",")
		outputFile << "], function() {\n"
		outputFile << "var __modules = arguments;\n"
		outputFile << bundledJavaScript
		outputFile << "});\n"
	}
}
