package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.Wrapper
import com.prezi.spaghetti.Wrapping
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 16/11/13.
 */
class Wrap extends AbstractBundleTask {

	@TaskAction
	wrap() {
		def config = readConfig()
		outputFile.parentFile.mkdirs()
		outputFile.delete()
		outputFile << Wrapper.wrap(config, type, inputFile.text)
	}

	Wrapping type
	void type(Object type) {
		if (type instanceof Wrapping) {
			this.type = type
		} else if (type instanceof String) {
			this.type = Wrapping.valueOf(type)
		} else {
			throw new IllegalArgumentException("Invalid type: ${type}")
		}
	}
}
