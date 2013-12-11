package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleConfigurationParser
import com.prezi.spaghetti.Wrapper
import com.prezi.spaghetti.Wrapping
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
		def wrappedJavaScript = createGenerator(config).processApplicationJavaScript(inputFile.text)

		outputFile.parentFile.mkdirs()
		outputFile.delete()
		outputFile << Wrapper.wrap(config, Wrapping.application, wrappedJavaScript)
	}
}
