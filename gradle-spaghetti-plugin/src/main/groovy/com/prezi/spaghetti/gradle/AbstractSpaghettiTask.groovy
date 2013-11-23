package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleConfigurationParser
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.InputFiles

/**
 * Created by lptr on 18/11/13.
 */
class AbstractSpaghettiTask extends DefaultTask {
	@Delegate
	Parameters params = new Parameters(project)

	protected Generator createGenerator(ModuleConfiguration config) {
		project.getPlugins().getPlugin(SpaghettiPlugin).createGeneratorForPlatform(platform, config)
	}

	public void applyParameters(Parameters params) {
		this.platform = params.platform
		this.configuration = params.configuration
		this.definition = params.definition
	}

	@InputFiles
	Configuration getConfiguration() {
		return params.configuration
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
			def moduleDefCtx = ModuleConfigurationParser.parse(definition)
			return moduleDefCtx
		}
		def config = ModuleConfigurationParser.parse(dependentDefinitionContexts, localDefinitionContexts)
		return config
	}
}
