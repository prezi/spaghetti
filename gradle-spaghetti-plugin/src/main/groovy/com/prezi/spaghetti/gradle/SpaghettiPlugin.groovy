package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.GeneratorFactory
import com.prezi.spaghetti.ModuleConfiguration
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by lptr on 12/11/13.
 */
class SpaghettiPlugin implements Plugin<Project> {
  static final String CONFIGURATION_NAME = "modules";
  static final String OBFUSCATED_CONFIGURATION_NAME = "modulesObf";

	private final Map<String, GeneratorFactory> generatorFactories = [:];

	@Override
	void apply(Project project)
	{
		for (factory in ServiceLoader.load(GeneratorFactory)) {
			generatorFactories.put factory.platform, factory
		}
		project.logger.info "Loaded generators for ${generatorFactories.keySet()}"

		def defaultConfiguration = project.configurations.findByName(CONFIGURATION_NAME)
		if (defaultConfiguration == null) {
			defaultConfiguration = project.configurations.create(CONFIGURATION_NAME)
		}

        if (project.configurations.findByName(OBFUSCATED_CONFIGURATION_NAME) == null) {
          project.configurations.create(OBFUSCATED_CONFIGURATION_NAME);
        }

		def extension = project.extensions.create "spaghetti", SpaghettiExtension, project, defaultConfiguration
		project.tasks.withType(AbstractSpaghettiTask) { AbstractSpaghettiTask task ->
			task.applyParameters(extension.params)
		}

		project.tasks.create("spaghetti-platforms") {
			doLast {
				if (generatorFactories.keySet().empty) {
					println "No platform support for Spaghetti is found"
				} else {
					println "Spaghetti supports the following platforms:\n"
					def length = generatorFactories.keySet().max { a, b -> a.length() <=> b.length() }.length()
					generatorFactories.values().each { factory ->
						println "  " + factory.platform.padRight(length) + " - " + factory.description
					}
				}
			}
		}
	}

	Generator createGeneratorForPlatform(String platform, ModuleConfiguration config)
	{
		GeneratorFactory generatorFactory = getGeneratorFactory(platform)
		return generatorFactory.createGenerator(config)
	}

	Map<FQName, FQName> getExterns(String platform) {
		GeneratorFactory generatorFactory = getGeneratorFactory(platform)
		return generatorFactory.getExternMapping().collectEntries([:]) { extern, impl ->
			return [ FQName.fromString(extern), FQName.fromString(impl) ]
		}
	}

	private GeneratorFactory getGeneratorFactory(String platform)
	{
		def generatorFactory = generatorFactories.get(platform)
		if (generatorFactory == null)
		{
			throw new IllegalArgumentException("No generator found for platform \"${platform}\". Supported platforms are: "
					+ generatorFactories.keySet().sort().join(", "))
		}
		generatorFactory
	}
}
