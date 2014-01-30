package com.prezi.spaghetti.gradle

import org.gradle.api.tasks.TaskAction
/**
 * Created by lptr on 12/11/13.
 */
class ConvertToJson extends AbstractSpaghettiTask {

	ConvertToJson()
	{
		this.outputDirectory = new File(project.buildDir, "spaghetti/json")
	}

	@TaskAction
	generate() {
		def config = readConfig()
		createGenerator(config).generateApplication(outputDirectory)
	}

	@Deprecated
	void namespace(String namespace) {
		logger.warn("The 'namespace' property is deprecated and will be removed in a future version. " +
				"It is not needed anymore, please remove.")
	}
}
