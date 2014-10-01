package com.prezi.spaghetti.gradle.internal;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.definition.ModuleConfigurationParser;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.InputFiles;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

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
		dependentModules(additionalDependentModules);
	}
	@Deprecated
	@SuppressWarnings("UnusedDeclaration")
	public void additionalDependentModule(Object... additionalDependentModules) {
		dependentModules(additionalDependentModules);
	}
	@Deprecated
	@SuppressWarnings("UnusedDeclaration")
	public void additionalDirectDependentModules(Object... additionalDependentModules) {
		dependentModules(additionalDependentModules);
	}
	@Deprecated
	@SuppressWarnings("UnusedDeclaration")
	public void additionalDirectDependentModule(Object... additionalDependentModules) {
		dependentModule(additionalDependentModules);
	}

	protected Set<ModuleBundle> lookupBundles() throws IOException {
		return ModuleBundleLookup.lookup(getDependentModules());
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
