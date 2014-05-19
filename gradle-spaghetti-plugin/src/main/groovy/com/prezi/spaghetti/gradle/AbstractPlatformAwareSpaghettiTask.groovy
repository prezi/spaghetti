package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.bundle.ModuleBundle
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
		def bundles = ModuleBundleLookup.lookupFromConfiguration(getDependentModules())
		def directSources = makeModuleSources(bundles.directBundles)
		def transitiveSources = makeModuleSources(bundles.transitiveBundles)
		def config = ModuleConfigurationParser.parse(
				localDefinitions,
				directSources,
				transitiveSources,
				Platforms.getExterns(getPlatform())
		)
		logger.info("Loaded configuration: ${config}")
		return config
	}

	private static List<ModuleDefinitionSource> makeModuleSources(Set<ModuleBundle> bundles) {
		return bundles.collect { ModuleBundle module ->
			return new ModuleDefinitionSource("module: " + module.name, module.definition)
		}
	}
}
