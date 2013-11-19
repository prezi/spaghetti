package com.prezi.spaghetti.gradle

import org.gradle.api.tasks.TaskAction
/**
 * Created by lptr on 12/11/13.
 */
class GenerateApplication extends AbstractGenerateTask {

	GenerateApplication()
	{
		this.outputDirectory = new File(project.buildDir, "spaghetti/application")
	}

	@TaskAction
	generate() {
		generator.generateApplication(readConfig(), outputDirectory)
	}
}
