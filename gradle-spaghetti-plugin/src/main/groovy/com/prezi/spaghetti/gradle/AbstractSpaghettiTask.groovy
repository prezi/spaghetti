package com.prezi.spaghetti.gradle

import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.InputFiles

/**
 * Created by lptr on 18/11/13.
 */
class AbstractSpaghettiTask extends ConventionTask {

	@InputFiles
	Configuration dependentModules
	void bundles(Configuration dependentModules) {
		this.dependentModules = dependentModules
	}

	ConfigurableFileCollection additionalDirectDependentModules = project.files()
	void additionalDirectDependentModules(Object... additionalDirectDependentModules) {
		this.additionalDirectDependentModules.from(*additionalDirectDependentModules)
	}
	void additionalDirectDependentModule(Object... additionalDirectDependentModules) {
		this.additionalDirectDependentModules(additionalDirectDependentModules)
	}

	@InputFiles
	FileCollection getAdditionalDirectDependentModules() {
		return project.files(this.additionalDirectDependentModules)
	}

	ConfigurableFileCollection additionalTransitiveDependentModules = project.files()
	void additionalTransitiveDependentModules(Object... additionalTransitiveDependentModules) {
		this.additionalTransitiveDependentModules.from(*additionalTransitiveDependentModules)
	}
	void additionalTransitiveDependentModule(Object... additionalTransitiveDependentModules) {
		this.additionalTransitiveDependentModules(additionalTransitiveDependentModules)
	}

	@InputFiles
	FileCollection getAdditionalTransitiveDependentModules() {
		return project.files(this.additionalTransitiveDependentModules)
	}

	protected SpaghettiPlugin getPlugin()
	{
		return project.getPlugins().getPlugin(SpaghettiPlugin)
	}

	protected ModuleBundleLookupResult lookupBundles() {
		def directFilesFromConfiguration = getDependentModules().resolvedConfiguration.firstLevelModuleDependencies*.moduleArtifacts*.file.flatten()
		def bundles = ModuleBundleLookup.lookup(
				directFilesFromConfiguration + getAdditionalDirectDependentModules().files,
				getDependentModules().files + getAdditionalTransitiveDependentModules().files)
		return bundles
	}
}
