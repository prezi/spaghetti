package com.prezi.spaghetti.gradle

import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.InputFiles

/**
 * Created by lptr on 18/11/13.
 */
class AbstractSpaghettiTask extends ConventionTask {

	@InputFiles
	Configuration dependentModules
	void bundles(Configuration dependentModules) {
		this.dependentModules = dependentModules
	}

	protected SpaghettiPlugin getPlugin()
	{
		return project.getPlugins().getPlugin(SpaghettiPlugin)
	}
}
