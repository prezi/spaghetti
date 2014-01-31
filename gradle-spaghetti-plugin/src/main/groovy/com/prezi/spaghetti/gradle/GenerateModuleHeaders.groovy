package com.prezi.spaghetti.gradle

import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
/**
 * Created by lptr on 12/11/13.
 */
class GenerateModuleHeaders extends AbstractGenerateTask {

	GenerateModuleHeaders()
	{
		this.outputDirectory = new File(project.buildDir, "spaghetti/module-headers")
	}

	@TaskAction
	generate() {
		def config = readConfig(definition)
		def moduleDef = config.localModules.first()
		logger.info("Generating module headers for ${moduleDef}")
		createGenerator(config).generateModuleHeaders(moduleDef, outputDirectory)
	}

	@Override
	@InputFile
	File getDefinition()
	{
		return super.getDefinition()
	}
}
