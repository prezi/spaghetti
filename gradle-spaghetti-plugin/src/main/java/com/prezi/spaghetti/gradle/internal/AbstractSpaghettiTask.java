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
	private ConfigurableFileCollection additionalDependentModulesInternal = getProject().files();

	@InputFiles
	public Configuration getDependentModules() {
		return dependentModules;
	}

	@InputFiles
	public ConfigurableFileCollection getAdditionalDependentModules() {
		return getProject().files(this.getAdditionalDependentModulesInternal());
	}

	public void setDependentModules(Configuration dependentModules) {
		this.dependentModules = dependentModules;
	}

	@SuppressWarnings("UnusedDeclaration")
	public void dependentModules(Configuration dependentModules) {
		setDependentModules(dependentModules);
	}

	public ConfigurableFileCollection getAdditionalDependentModulesInternal() {
		return additionalDependentModulesInternal;
	}

	public void additionalDependentModules(Object... additionalDependentModules) {
		this.getAdditionalDependentModulesInternal().from(additionalDependentModules);
	}

	@SuppressWarnings("UnusedDeclaration")
	public void additionalDependentModule(Object... additionalDependentModules) {
		this.additionalDependentModules(additionalDependentModules);
	}

	@Deprecated
	@SuppressWarnings("UnusedDeclaration")
	public ConfigurableFileCollection getAdditionalDirectDependentModules() {
		return getAdditionalDependentModules();
	}
	@Deprecated
	@SuppressWarnings("UnusedDeclaration")
	public void additionalDirectDependentModules(Object... additionalDependentModules) {
		additionalDependentModules(additionalDependentModules);
	}
	@Deprecated
	@SuppressWarnings("UnusedDeclaration")
	public void additionalDirectDependentModule(Object... additionalDependentModules) {
		additionalDependentModule(additionalDependentModules);
	}

	protected Set<ModuleBundle> lookupBundles() throws IOException {
		Set<File> dependencies = Sets.newLinkedHashSet();
		for (ResolvedDependency dependency : getDependentModules().getResolvedConfiguration().getFirstLevelModuleDependencies()) {
			for (ResolvedArtifact artifact : dependency.getModuleArtifacts()) {
				dependencies.add(artifact.getFile());
			}
		}

		dependencies.addAll(getAdditionalDependentModules().getFiles());

		return ModuleBundleLookup.lookup(dependencies);
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
		Collection<ModuleDefinitionSource> sources = makeModuleSources(lookupBundles());
		ModuleConfiguration config = ModuleConfigurationParser.parse(localDefinition, sources);
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
