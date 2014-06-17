package com.prezi.spaghetti.gradle

import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
class GenerateHeaders extends AbstractDefinitionAwareSpaghettiTask {

	@OutputDirectory
	File outputDirectory

	void outputDirectory(Object directory) {
		this.outputDirectory = project.file(directory)
	}

	GenerateHeaders()
	{
		this.conventionMapping.outputDirectory = { new File(project.buildDir, "spaghetti/generated-headers") }
	}

	@TaskAction
	generate() {
		def config = readConfig(getDefinitions())
		logger.info("Generating module headers for ${config.localModules.join(", ")}")

		def directory = getOutputDirectory()
		directory.deleteDir()
		directory.mkdirs()
		createGenerator(config).generateHeaders(directory)
	}
}
