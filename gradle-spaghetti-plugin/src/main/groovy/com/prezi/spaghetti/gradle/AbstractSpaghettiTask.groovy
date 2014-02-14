package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleConfigurationParser
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.InputFiles

/**
 * Created by lptr on 18/11/13.
 */
class AbstractSpaghettiTask extends ConventionTask {
	@Delegate
	Parameters params = new Parameters(project)

	protected SpaghettiPlugin getPlugin()
	{
		return project.getPlugins().getPlugin(SpaghettiPlugin)
	}

	protected Generator createGenerator(ModuleConfiguration config) {
		return plugin.createGeneratorForPlatform(platform, config)
	}

	@InputFiles
	Configuration getConfiguration() {
		return params.configuration
	}

	ModuleConfiguration readConfig(File... files) {
		readConfig(files.collectEntries() { file ->
			[ file.toString(), file.text ]
		})
	}

	ModuleConfiguration readConfig(Map<String, String> localDefinitions = [:]) {
		def dependentDefinitionContexts
		if (configuration != null) {
			dependentDefinitionContexts = ModuleDefinitionLookup.getAllDefinitions(configuration)
		} else {
			dependentDefinitionContexts = []
		}
		def localDefinitionContexts = localDefinitions.collect { location, definition ->
			def moduleDefCtx = ModuleConfigurationParser.parse(definition, location)
			return moduleDefCtx
		}
		def config = ModuleConfigurationParser.parse(
				dependentDefinitionContexts,
				localDefinitionContexts,
				plugin.getExterns(platform),
				String.valueOf(project.version),
				this.sourceBaseUrl
		)
		logger.info("Loaded configuration: ${config}")
		return config
	}
}
