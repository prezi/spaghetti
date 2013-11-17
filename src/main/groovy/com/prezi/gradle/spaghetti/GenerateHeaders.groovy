package com.prezi.gradle.spaghetti

import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 12/11/13.
 */
class GenerateHeaders extends AbstractGenerateTask {
	@InputFile
	File definition

	@TaskAction
	generate() {
		def definitions
		if (configuration != null) {
			definitions = ModuleDefinitionLookup.getAllDefinitions(configuration)
		} else {
			definitions = []
		}
		def moduleDefCtx = ModuleParser.parse(definition.text)
		definitions += moduleDefCtx
		def config = ModuleConfigurationParser.parse(definitions)
		def moduleDef = config.modules.values().find { module -> module.context == moduleDefCtx }
		generator.generateInterfaces(config, moduleDef, outputDirectory)
	}

	void definition(Object file) {
		this.definition = project.file(file)
	}

	@Override
	@Optional
	@InputFiles
	Configuration getConfiguration()
	{
		return super.getConfiguration()
	}
}
