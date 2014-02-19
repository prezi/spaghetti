package com.prezi.spaghetti.gradle

import org.gradle.api.tasks.TaskAction
/**
 * Created by lptr on 12/11/13.
 */
class GenerateHeaders extends AbstractGenerateTask {

	GenerateHeaders()
	{
		this.conventionMapping.outputDirectory = { new File(project.buildDir, "spaghetti/generated-headers") }
	}

	@TaskAction
	generate() {
		def config = readConfig(getModuleDefinitions())
		logger.info("Generating module headers for ${config.localModules.join(", ")}")
		createGenerator(config).generateHeaders(getOutputDirectory())
	}
}
