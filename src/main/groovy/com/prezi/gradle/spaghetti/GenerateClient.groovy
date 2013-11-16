package com.prezi.gradle.spaghetti

import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 12/11/13.
 */
class GenerateClient extends AbstractGenerateTask {
	@InputFiles
	Configuration configuration

	@TaskAction
	generate() {
		def definitions = configuration.files.collect { File file ->
			def module
			try {
				logger.info(">>>>>>>> Trying to load module from ${file}")
				module = ModuleBundle.load(file)
			} catch (e) {
				logger.debug("!>>>>>>> ERROR", e)
				return null
			}
			return ModuleParser.parse(module.definition)
		} - null

		def config = ModuleConfigurationParser.parse(definitions)
		println "Config: ${config}"
		generator.generateClientModule(config, outputDirectory)
	}

	def configuration(Configuration configuration) {
		this.configuration = configuration
	}
}
