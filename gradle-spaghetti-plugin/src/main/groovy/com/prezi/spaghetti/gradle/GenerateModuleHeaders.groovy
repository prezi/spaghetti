package com.prezi.spaghetti.gradle

import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
/**
 * Created by lptr on 12/11/13.
 */
class GenerateModuleHeaders extends AbstractGenerateTask {

	GenerateModuleHeaders()
	{
		this.conventionMapping.outputDirectory = { new File(project.buildDir, "spaghetti/module-headers") }
	}

	@TaskAction
	generate() {
		def config = readConfig(getDefinition())
		def moduleDef = config.localModules.first()
		logger.info("Generating module headers for ${moduleDef}")
		createGenerator(config).generateModuleHeaders(moduleDef, getOutputDirectory())
	}

	@Override
	@InputFile
	File getDefinition()
	{
		return super.getDefinition()
	}
}
