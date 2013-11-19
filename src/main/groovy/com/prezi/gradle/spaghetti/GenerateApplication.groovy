package com.prezi.gradle.spaghetti

import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
/**
 * Created by lptr on 12/11/13.
 */
class GenerateApplication extends AbstractGenerateTask {
	@TaskAction
	generate() {
		generator.generateApplication(readConfig(), outputDirectory)
	}

	@Override
	@InputFiles
	Configuration getConfiguration()
	{
		return super.getConfiguration()
	}
}
