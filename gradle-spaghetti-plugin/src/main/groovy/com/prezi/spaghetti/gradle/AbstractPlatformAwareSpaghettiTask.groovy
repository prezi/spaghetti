package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.definition.ModuleConfigurationParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import com.prezi.spaghetti.Platforms
import org.gradle.api.tasks.Input

/**
 * Created by lptr on 19/04/14.
 */
class AbstractPlatformAwareSpaghettiTask extends AbstractSpaghettiTask {
	@Input
	String platform

	protected Generator createGenerator(ModuleConfiguration config) {
		return Platforms.createGeneratorForPlatform(getPlatform(), config)
	}

	ModuleConfiguration readConfig(Iterable<File> files) {
		readConfigInternal(files.collect() { file ->
			new ModuleDefinitionSource(file.toString(), file.text)
		})
	}

	ModuleConfiguration readConfig() {
		readConfigInternal([])
	}

	private ModuleConfiguration readConfigInternal(Collection<ModuleDefinitionSource> localDefinitions) {
		def dependentDefinitions = ModuleDefinitionLookup.getAllDefinitionSources(getBundles())
		def config = ModuleConfigurationParser.parse(
				dependentDefinitions,
				localDefinitions,
				Platforms.getExterns(getPlatform())
		)
		logger.info("Loaded configuration: ${config}")
		return config
	}
}
