package com.prezi.spaghetti.gradle

import org.gradle.api.tasks.TaskAction
/**
 * Created by lptr on 12/11/13.
 */
class GenerateApplication extends AbstractGenerateTask {

	String namespace

	GenerateApplication()
	{
		this.outputDirectory = new File(project.buildDir, "spaghetti/application")
	}

	@TaskAction
	generate() {
		if (namespace == null || namespace.length() == 0) {
			throw new IllegalArgumentException("Parameter \"namespace\" is not specified")
		}
		generator.generateApplication(readConfig(), namespace, outputDirectory)
	}

	void namespace(String namespace) {
		this.namespace = namespace
	}
}
