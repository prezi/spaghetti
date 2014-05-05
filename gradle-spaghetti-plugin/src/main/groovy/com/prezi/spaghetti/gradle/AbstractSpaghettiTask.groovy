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

	ConfigurableFileCollection bundles = project.files()
	void bundles(Object... bundles) {
		this.bundles.from(*bundles)
	}
	void bundle(Object... bundles) {
		this.bundles(*bundles)
	}

	@InputFiles
	FileCollection getBundles() {
		return project.files(this.bundles)
	}

	protected SpaghettiPlugin getPlugin()
	{
		return project.getPlugins().getPlugin(SpaghettiPlugin)
	}
}
