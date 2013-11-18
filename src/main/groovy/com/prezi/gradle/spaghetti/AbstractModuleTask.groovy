package com.prezi.gradle.spaghetti

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration

/**
 * Created by lptr on 18/11/13.
 */
class AbstractModuleTask extends DefaultTask {
	protected Configuration configuration

	String platform

	Generator getGenerator() {
		project.getPlugins().getPlugin(SpaghettiPlugin).getGeneratorForPlatform(platform)
	}

	ModuleConfiguration readConfig(String... localDefinitions) {
		return readConfig(localDefinitions.toList())
	}

	ModuleConfiguration readConfig(Iterable<String> localDefinitions) {
		def dependentDefinitionContexts
		if (configuration != null) {
			dependentDefinitionContexts = ModuleDefinitionLookup.getAllDefinitions(configuration)
		} else {
			dependentDefinitionContexts = []
		}
		def localDefinitionContexts = localDefinitions.collect { String definition ->
			def moduleDefCtx = ModuleParser.parse(definition)
			return moduleDefCtx
		}
		def config = ModuleConfigurationParser.parse(dependentDefinitionContexts, localDefinitionContexts)
		return config
	}

	void configuration(Configuration configuration) {
		this.configuration = configuration
	}

	Configuration getConfiguration() {
		return configuration
	}
}
