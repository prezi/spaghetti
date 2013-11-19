package com.prezi.spaghetti.gradle

import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile

/**
 * Created by lptr on 19/11/13.
 */
class AbstractBundleTask extends AbstractSpaghettiTask {
	@InputFile
	File inputFile

	def inputFile(Object f) {
		this.inputFile = project.file(f)
	}

	@OutputFile
	File outputFile

	def outputFile(Object f) {
		this.outputFile = project.file(f)
	}
}
