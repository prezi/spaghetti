package com.prezi.spaghetti.gradle.internal;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.definition.ModuleConfigurationParser;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedDependency;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.InputFiles;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class AbstractSpaghettiTask extends ConventionTask {
	private Configuration dependentModules;
	private ConfigurableFileCollection additionalDirectDependentModulesInternal = getProject().files();
	private ConfigurableFileCollection additionalTransitiveDependentModulesInternal = getProject().files();

	@InputFiles
	public Configuration getDependentModules() {
		return dependentModules;
	}

	public void setDependentModules(Configuration dependentModules) {
		this.dependentModules = dependentModules;
	}

	@SuppressWarnings("UnusedDeclaration")
	public void dependentModules(Configuration dependentModules) {
		setDependentModules(dependentModules);
	}

	public ConfigurableFileCollection getAdditionalDirectDependentModulesInternal() {
		return additionalDirectDependentModulesInternal;
	}

	public void additionalDirectDependentModules(Object... additionalDirectDependentModules) {
		this.getAdditionalDirectDependentModulesInternal().from(additionalDirectDependentModules);
	}

	@SuppressWarnings("UnusedDeclaration")
	public void additionalDirectDependentModule(Object... additionalDirectDependentModules) {
		this.additionalDirectDependentModules(additionalDirectDependentModules);
	}

	@SuppressWarnings("UnusedDeclaration")
	public void additionalDependentModules(Object... modules) {
		additionalDirectDependentModules(modules);
	}

	@SuppressWarnings("UnusedDeclaration")
	public void additionalDependentModule(Object... modules) {
		additionalDirectDependentModules(modules);
	}

	@InputFiles
	public ConfigurableFileCollection getAdditionalDirectDependentModules() {
		return getProject().files(this.getAdditionalDirectDependentModulesInternal());
	}

	public ConfigurableFileCollection getAdditionalTransitiveDependentModulesInternal() {
		return additionalTransitiveDependentModulesInternal;
	}

	public void additionalTransitiveDependentModules(Object... additionalTransitiveDependentModules) {
		this.getAdditionalTransitiveDependentModulesInternal().from(additionalTransitiveDependentModules);
	}

	@SuppressWarnings("UnusedDeclaration")
	public void additionalTransitiveDependentModule(Object... additionalTransitiveDependentModules) {
		this.additionalTransitiveDependentModules(additionalTransitiveDependentModules);
	}

	@InputFiles
	public ConfigurableFileCollection getAdditionalTransitiveDependentModules() {
		return getProject().files(this.getAdditionalTransitiveDependentModulesInternal());
	}

	protected ModuleBundleLookupResult lookupBundles() throws IOException {
		Set<File> directDependencies = Sets.newLinkedHashSet();
		for (ResolvedDependency dependency : getDependentModules().getResolvedConfiguration().getFirstLevelModuleDependencies()) {
			for (ResolvedArtifact artifact : dependency.getModuleArtifacts()) {
				directDependencies.add(artifact.getFile());
			}
		}

		directDependencies.addAll(getAdditionalDirectDependentModules().getFiles());

		Set<File> transitiveDependencies = Sets.newLinkedHashSet(getDependentModules().getFiles());
		transitiveDependencies.addAll(getAdditionalTransitiveDependentModules().getFiles());

		return ModuleBundleLookup.lookup(directDependencies, transitiveDependencies);
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
		ModuleBundleLookupResult bundles = lookupBundles();
		Collection<ModuleDefinitionSource> directSources = makeModuleSources(bundles.getDirectBundles());
		Collection<ModuleDefinitionSource> transitiveSources = makeModuleSources(bundles.getTransitiveBundles());
		ModuleConfiguration config = ModuleConfigurationParser.parse(localDefinition, directSources, transitiveSources);
		getLogger().info("Loaded configuration: {}", config);
		return config;
	}

	private static Collection<ModuleDefinitionSource> makeModuleSources(Set<ModuleBundle> bundles) {
		return Collections2.transform(bundles, new Function<ModuleBundle, ModuleDefinitionSource>() {
			@Override
			public ModuleDefinitionSource apply(ModuleBundle bundle) {
				try {
					return ModuleDefinitionSource.fromBundle(bundle);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
}
