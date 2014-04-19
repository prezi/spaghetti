package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.Wrapper
import com.prezi.spaghetti.Wrapping
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 16/11/13.
 */
class Wrap extends AbstractSpaghettiTask {

	@Delegate InputOutput inputOutput = new InputOutput(project)

	@TaskAction
	wrap() {
		def config = readConfig()
		def outputFile = getOutputFile()
		outputFile.parentFile.mkdirs()
		outputFile.delete()
		outputFile << Wrapper.wrap(config, type, getInputFile().text)
	}

	@Input
	Wrapping type = Wrapping.module
	void type(Object type) {
		if (type == "nodeApp") {
			logger.warn("The value 'nodeApp' for wrapping type is deprecated, and is going to be removed in a future version. Please use 'nodeModule' instead.")
			type = Wrapping.nodeModule
		}

		if (type instanceof Wrapping) {
			this.type = type
		} else if (type instanceof String) {
			this.type = Wrapping.valueOf(type)
		} else {
			throw new IllegalArgumentException("Invalid type: ${type}")
		}
	}
}
