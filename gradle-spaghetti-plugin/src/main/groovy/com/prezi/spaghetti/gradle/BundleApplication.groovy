package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.Wrapper
import com.prezi.spaghetti.Wrapping
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 16/11/13.
 */
class BundleApplication extends AbstractSpaghettiTask {

	@Delegate InputOutput inputOutput = new InputOutput(project)

	BundleApplication()
	{
		this.conventionMapping.inputFile = { new File(project.buildDir, "application.js") }
		this.conventionMapping.outputFile = { new File(project.buildDir, "spaghetti/application.js") }
	}

	@TaskAction
	bundle() {
		def config = readConfig()
		def wrappedJavaScript = createGenerator(config).processApplicationJavaScript(getInputFile().text)

		def outputFile = getOutputFile()
		outputFile.parentFile.mkdirs()
		outputFile.delete()
		outputFile << Wrapper.wrap(config, Wrapping.application, wrappedJavaScript)
	}
}
