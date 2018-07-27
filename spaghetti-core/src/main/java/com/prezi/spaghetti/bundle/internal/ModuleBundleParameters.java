package com.prezi.spaghetti.bundle.internal;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.prezi.spaghetti.bundle.DefinitionLanguage;
import com.prezi.spaghetti.bundle.ModuleFormat;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * Parameters for creating module bundles.
 */
public class ModuleBundleParameters {
	public final String name;
	public final String definition;
	public final DefinitionLanguage definitionLang;
	public final String version;
	public final ModuleFormat format;
	public final String sourceBaseUrl;
	public final String javaScript;
	public final String sourceMap;
	public final SortedSet<String> dependentModules;
	public final SortedSet<String> lazyDependentModules;
	public final SortedMap<String, String> externalDependencies;
	public final Boolean lazyLoadable;
	public final File resourcesDirectory;

	public ModuleBundleParameters(String name, String definition, DefinitionLanguage definitionLang, String version, ModuleFormat format, String sourceBaseUrl, String javaScript, String sourceMap, Collection<String> dependentModules, Collection<String> lazyDependentModules, Map<String, String> externalDependencies, File resourcesDirectory, Boolean lazyLoadable) {
		this.name = name;
		this.definition = definition;
		this.definitionLang = definitionLang;
		this.version = version;
		this.format = format;
		this.sourceBaseUrl = sourceBaseUrl;
		this.javaScript = javaScript;
		this.sourceMap = sourceMap;
		this.dependentModules = ImmutableSortedSet.copyOf(dependentModules);
		this.lazyDependentModules = ImmutableSortedSet.copyOf(lazyDependentModules);
		this.externalDependencies = ImmutableSortedMap.copyOf(externalDependencies);
		this.resourcesDirectory = resourcesDirectory;
		this.lazyLoadable = lazyLoadable;
	}
}
