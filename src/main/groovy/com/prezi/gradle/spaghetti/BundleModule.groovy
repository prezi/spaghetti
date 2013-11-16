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
	File inputFile

	@OutputFile
	File outputFile

	@TaskAction
	bundle() {
		outputFile << "define(function() { var __module;\n"
		outputFile << inputFile.text
		outputFile << "return __module;});"
	}

	def inputFile(Object f) {
		this.inputFile = project.file(f)
	}

	def outputFile(Object f) {
		this.outputFile = project.file(f)
	}
}
