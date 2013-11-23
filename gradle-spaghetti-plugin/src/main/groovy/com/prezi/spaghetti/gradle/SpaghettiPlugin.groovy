package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.GeneratorFactory
import com.prezi.spaghetti.ModuleConfiguration
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by lptr on 12/11/13.
 */
class SpaghettiPlugin implements Plugin<Project> {
	static final String CONFIGURATION_NAME = "modules"

	private final Map<String, GeneratorFactory> generatorFactories = [:];

	@Override
	void apply(Project project)
	{
		for (generator in ServiceLoader.load(GeneratorFactory)) {
			generatorFactories.put generator.platform, generator
		}
		project.logger.info "Loaded generators for ${generatorFactories.keySet()}"

		def defaultConfiguration = project.configurations.findByName(CONFIGURATION_NAME)
		if (defaultConfiguration == null) {
			defaultConfiguration = project.configurations.create(CONFIGURATION_NAME)
		}

		def extension = project.extensions.create "spaghetti", ModulesExtension, project, defaultConfiguration
		project.tasks.withType(AbstractSpaghettiTask) { AbstractSpaghettiTask task ->
			task.applyParameters(extension.params)
		}
	}

	Generator createGeneratorForPlatform(String platform, ModuleConfiguration config)
	{
		def generatorFactory = generatorFactories.get(platform)
		if (generatorFactory == null) {
			throw new IllegalArgumentException("No generator found for platform \"${platform}\". Supported platforms are: "
					+ generatorFactories.keySet().sort().join(", "))
		}
		return generatorFactory.createGenerator(config)
	}
}
