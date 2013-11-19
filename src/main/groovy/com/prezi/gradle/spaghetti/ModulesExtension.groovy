package com.prezi.gradle.spaghetti

import org.gradle.api.Project

/**
 * Created by lptr on 19/11/13.
 */
class ModulesExtension {
	@Delegate
	Parameters params

	ModulesExtension(Project project) {
		this.params = new Parameters(project)
	}
}
