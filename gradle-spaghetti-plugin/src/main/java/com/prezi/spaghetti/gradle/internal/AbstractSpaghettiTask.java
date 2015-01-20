package com.prezi.spaghetti.gradle.internal;

import com.prezi.spaghetti.bundle.ModuleBundleSet;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.definition.ModuleConfigurationParser;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import com.prezi.spaghetti.internal.DeprecationNagger;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.InputFiles;

import java.io.File;
import java.io.IOException;

public class AbstractSpaghettiTask extends ConventionTask {
	private ConfigurableFileCollection dependentModules = getProject().files();

	@InputFiles
	public ConfigurableFileCollection getDependentModules() {
		return dependentModules;
	}

	public void setDependentModules(ConfigurableFileCollection dependentModules) {
		this.dependentModules = dependentModules;
	}

	public void dependentModules(Object... additionalDependentModules) {
		ConfigurableFileCollection dependentModules = getDependentModules();
		dependentModules.from(additionalDependentModules);
		setDependentModules(dependentModules);
	}

	public void dependentModule(Object... dependentModules) {
		dependentModules(dependentModules);
	}

	@Deprecated
	@SuppressWarnings("UnusedDeclaration")
	public void additionalDependentModules(Object... additionalDependentModules) {
		DeprecationNagger.nagUserOfReplacedMethod("additionalDependentModules", "dependentModules");
		dependentModules(additionalDependentModules);
	}
	@Deprecated
	@SuppressWarnings("UnusedDeclaration")
	public void additionalDependentModule(Object... additionalDependentModules) {
		DeprecationNagger.nagUserOfReplacedMethod("additionalDependentModule", "dependentModule");
		dependentModules(additionalDependentModules);
	}
	@Deprecated
	@SuppressWarnings("UnusedDeclaration")
	public void additionalDirectDependentModules(Object... additionalDependentModules) {
		DeprecationNagger.nagUserOfReplacedMethod("additionalDirectDependentModules", "dependentModules");
		dependentModules(additionalDependentModules);
	}
	@Deprecated
	@SuppressWarnings("UnusedDeclaration")
	public void additionalDirectDependentModule(Object... additionalDependentModules) {
		DeprecationNagger.nagUserOfReplacedMethod("additionalDirectDependentModule", "dependentModule");
		dependentModule(additionalDependentModules);
	}

	protected ModuleBundleSet lookupBundles() throws IOException {
		return ModuleBundleLookup.lookup(getProject(), getDependentModules());
	}

	public ModuleConfiguration readConfig(File definition) throws IOException {
		ModuleDefinitionSource definitionSource;
		try {
			definitionSource = ModuleDefinitionSource.fromFile(definition);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return readConfigInternal(definitionSource);
	}

	private ModuleConfiguration readConfigInternal(ModuleDefinitionSource localDefinition) throws IOException {
		ModuleBundleSet bundles = lookupBundles();
		ModuleConfiguration config = ModuleConfigurationParser.parse(localDefinition, bundles);
		getLogger().info("Loaded configuration: {}", config);
		return config;
	}
}
