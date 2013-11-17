package com.prezi.gradle.spaghetti

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
/**
 * Created by lptr on 12/11/13.
 */
abstract class AbstractGenerateTask extends DefaultTask {

	Configuration configuration

	String platform

	@OutputDirectory
	File outputDirectory

	Generator getGenerator() {
		project.getPlugins().getPlugin(SpaghettiPlugin).generators.get(platform)
	}

	void outputDirectory(Object directory) {
		this.outputDirectory = project.file(directory)
	}

	void configuration(Configuration configuration) {
		this.configuration = configuration
	}
}
