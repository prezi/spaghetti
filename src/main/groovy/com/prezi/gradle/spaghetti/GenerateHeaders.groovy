package com.prezi.gradle.spaghetti

import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 12/11/13.
 */
class GenerateHeaders extends AbstractGenerateTask {

	@InputFile
	File definition

	@TaskAction
	generate() {
		def moduleDefCtx = ModuleParser.parse(definition.text)
		def config = ModuleConfigurationParser.parse(moduleDefCtx)

		generator.generateInterfaces(config, outputDirectory)
	}

	void definition(Object file) {
		this.definition = project.file(file)
	}
}
