package com.prezi.gradle.spaghetti

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by lptr on 12/11/13.
 */
class SpaghettiPlugin implements Plugin<Project> {
	private final Map<String, Generator> generators = [:];

	@Override
	void apply(Project project)
	{
		for (generator in ServiceLoader.load(Generator)) {
			generators.put generator.platform, generator
		}
		project.logger.info "Loaded generators for ${generators.keySet()}"
	}

	Generator getGeneratorForPlatform(String platform)
	{
		def generator = generators.get(platform)
		if (generator == null) {
			throw new IllegalArgumentException("No generator found for platform \"${platform}\". Supported platforms are: "
					+ generators.keySet().sort().join(", "))
		}
		return generator
	}
}
