package com.prezi.spaghetti.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

/**
 * Created by lptr on 19/11/13.
 */
class ModulesExtension {
	@Delegate
	Parameters params

	ModulesExtension(Project project, Configuration defaultConfiguration) {
		this.params = new Parameters(project)
		params.configuration = defaultConfiguration
	}
}
