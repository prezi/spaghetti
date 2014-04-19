package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleConfigurationParser
import com.prezi.spaghetti.ModuleDefinitionSource
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional

/**
 * Created by lptr on 18/11/13.
 */
class AbstractSpaghettiTask extends ConventionTask {

	@Input
	String platform

	ConfigurableFileCollection bundles = project.files()
	void bundle(Object... bundles) {
		this.bundles.from(*bundles)
	}

	@InputFiles
	FileCollection getBundles() {
		return project.files(this.bundles)
	}

	protected SpaghettiPlugin getPlugin()
	{
		return project.getPlugins().getPlugin(SpaghettiPlugin)
	}

	protected Generator createGenerator(ModuleConfiguration config) {
		return Platform.createGeneratorForPlatform(getPlatform(), config)
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
				Platform.getExterns(getPlatform())
		)
		logger.info("Loaded configuration: ${config}")
		return config
	}
}
