package com.prezi.gradle.spaghetti

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by lptr on 12/11/13.
 */
class SpaghettiPlugin implements Plugin<Project> {
	Map<String, Generator> generators = [:];

	@Override
	void apply(Project project)
	{
		for (generator in ServiceLoader.load(Generator)) {
			generators.put generator.platform, generator
			generator.initialize(project)
		}
		project.logger.info "Loaded generators for ${generators.keySet()}"
	}
}
