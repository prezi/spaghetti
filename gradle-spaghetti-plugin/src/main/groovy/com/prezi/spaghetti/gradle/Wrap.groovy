package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.Wrapper
import com.prezi.spaghetti.Wrapping
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 16/11/13.
 */
class Wrap extends AbstractSpaghettiTask {

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

	@Input
	@Optional
	String modulesDirectory = ""

	void modulesDirectory(String modulesDirectory) {
		this.modulesDirectory = modulesDirectory
	}

	@TaskAction
	wrap() {
		def outputFile = getOutputFile()
		outputFile.parentFile.mkdirs()
		outputFile.delete()

		def dependentModuleNames = ModuleDefinitionLookup.getAllBundles(getBundles())*.name
		outputFile << Wrapper.wrapWithConfig(dependentModuleNames, type, getModulesDirectory(), getInputFile().text)
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
