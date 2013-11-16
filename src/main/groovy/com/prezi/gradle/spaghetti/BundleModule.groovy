package com.prezi.gradle.spaghetti

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
/**
 * Created by lptr on 16/11/13.
 */
class BundleModule extends DefaultTask {
	@InputFile
	File definition

	@InputFile
	File inputFile

	@OutputFile
	File outputFile

	@TaskAction
	bundle() {
		new ModuleBundle(definition.text, inputFile.text).save(outputFile)
	}

	def definition(Object f) {
		this.definition = project.file(f)
	}

	def inputFile(Object f) {
		this.inputFile = project.file(f)
	}

	def outputFile(Object f) {
		this.outputFile = project.file(f)
	}
}
