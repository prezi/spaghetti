package com.prezi.gradle.spaghetti

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by lptr on 12/11/13.
 */
class SpaghettiPlugin implements Plugin<Project> {
	static final String CONFIGURATION_NAME = "modules"

	private final Map<String, Generator> generators = [:];

	@Override
	void apply(Project project)
	{
		for (generator in ServiceLoader.load(Generator)) {
			generators.put generator.platform, generator
		}
		project.logger.info "Loaded generators for ${generators.keySet()}"

		def defaultConfiguration = project.configurations.findByName(CONFIGURATION_NAME)
		if (defaultConfiguration == null) {
			defaultConfiguration = project.configurations.create(CONFIGURATION_NAME)
		}

		def extension = project.extensions.create "spaghetti", ModulesExtension, project, defaultConfiguration
		project.tasks.withType(AbstractSpaghettiTask) { AbstractSpaghettiTask task ->
			task.applyParameters(extension.params)
		}
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
