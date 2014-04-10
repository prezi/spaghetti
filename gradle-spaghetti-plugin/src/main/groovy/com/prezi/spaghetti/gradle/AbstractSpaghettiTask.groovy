package com.prezi.spaghetti.gradle

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
		return Platform.createGeneratorForPlatform(platform, config)
	}

	@InputFiles
	Configuration getConfiguration() {
		return params.configuration
	}

	ModuleConfiguration readConfig(Collection<File> files) {
		readConfig(files.collectEntries() { file ->
			[ file.toString(), file.text ]
		})
	}

	ModuleConfiguration readConfig(Map<String, String> localDefinitions = [:]) {
		def dependentDefinitionContexts
		if (getConfiguration()) {
			dependentDefinitionContexts = ModuleDefinitionLookup.getAllDefinitions(getConfiguration())
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
				Platform.getExterns(platform),
				String.valueOf(project.version),
				getSourceBaseUrl()
		)
		logger.info("Loaded configuration: ${config}")
		return config
	}

	@InputFiles
	Set<File> definitions = []
	public void definition(Object definitions)
	{
		this.definitions += project.files(definitions).collect();
	}

	public Set<File> getDefinitions() {
		return this.definitions;
	}
}
