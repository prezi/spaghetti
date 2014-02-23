package com.prezi.spaghetti.gradle

import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Created by lptr on 17/11/13.
 */
class ExtractModules extends AbstractSpaghettiTask {
	@OutputDirectory
	File outputDirectory

	void outputDirectory(Object directory) {
		this.outputDirectory = project.file(directory)
	}

	ExtractModules() {
		this.conventionMapping.outputDirectory = { new File(project.buildDir, "spaghetti/modules") }
	}

	@TaskAction
	extract() {
		ModuleExtractor.extractModules(getConfiguration(), getOutputDirectory())
	}
}
