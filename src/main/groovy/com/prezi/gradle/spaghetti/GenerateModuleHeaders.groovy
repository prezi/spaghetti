package com.prezi.gradle.spaghetti

import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 12/11/13.
 */
class GenerateModuleHeaders extends AbstractGenerateTask {
	@InputFile
	File definition

	@TaskAction
	generate() {
		def config = readConfig(definition.text)
		def moduleDef = config.localModules.first()
		generator.generateModuleHeaders(config, moduleDef, outputDirectory)
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
