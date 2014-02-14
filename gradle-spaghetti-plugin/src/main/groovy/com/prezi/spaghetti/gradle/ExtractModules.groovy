package com.prezi.spaghetti.gradle

import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 17/11/13.
 */
class ExtractModules extends AbstractGenerateTask {
	ExtractModules() {
		this.conventionMapping.outputDirectory = { new File(project.buildDir, "spaghetti/modules") }
	}

	@TaskAction
	extract() {
		ModuleExtractor.extractModules(getConfiguration(), getOutputDirectory())
	}
}
