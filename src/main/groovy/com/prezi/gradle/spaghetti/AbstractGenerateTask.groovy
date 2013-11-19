package com.prezi.gradle.spaghetti

import org.gradle.api.tasks.OutputDirectory
/**
 * Created by lptr on 12/11/13.
 */
abstract class AbstractGenerateTask extends AbstractSpaghettiTask {

	@OutputDirectory
	File outputDirectory

	void outputDirectory(Object directory) {
		this.outputDirectory = project.file(directory)
	}
}
