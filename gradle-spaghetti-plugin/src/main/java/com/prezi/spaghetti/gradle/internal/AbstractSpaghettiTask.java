package com.prezi.spaghetti.gradle.internal;

import com.google.common.collect.Sets;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleSet;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import com.prezi.spaghetti.definition.internal.DefaultModuleDefinitionSource;
import com.prezi.spaghetti.definition.internal.ModuleConfigurationParser;
import com.prezi.spaghetti.internal.DeprecationNagger;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;

public class AbstractSpaghettiTask extends ConventionTask {
	private ConfigurableFileCollection dependentModules = getProject().files();
	private ModuleBundleSet dependentBundles;

	@InputFiles
	public ConfigurableFileCollection getDependentModules() {
		return dependentModules;
	}

	/**
	 * Returns names of directly dependent modules. This is here so that Gradle will detect
	 * a change in inputs even if the only change is that a previously directly dependent
	 * module becomes transitively dependent, or vice versa.
	 *
	 * @return names of directly dependent modules.
	 * @throws IOException
	 */
	@Input
	@SuppressWarnings("UnusedDeclaration")
	protected SortedSet<String> getDirectDependentBundleNames() throws IOException {
		SortedSet<String> directBundleNames = Sets.newTreeSet();
		for (ModuleBundle bundle : lookupBundles().getDirectBundles()) {
			directBundleNames.add(bundle.getName());
		}
		return directBundleNames;
	}

	public void setDependentModules(ConfigurableFileCollection dependentModules) {
		this.dependentBundles = null;
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
		if (dependentBundles == null) {
			dependentBundles = ModuleBundleLookup.lookup(getProject(), getDependentModules());
		}
		return dependentBundles;
	}

	public ModuleConfiguration readConfig(File definition) throws IOException {
		ModuleDefinitionSource definitionSource;
		try {
			definitionSource = DefaultModuleDefinitionSource.fromFile(definition);
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
