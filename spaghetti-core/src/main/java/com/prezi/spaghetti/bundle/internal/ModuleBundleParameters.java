package com.prezi.spaghetti.bundle.internal;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
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
	public final String version;
	public final ModuleFormat format;
	public final String sourceBaseUrl;
	public final String javaScript;
	public final String sourceMap;
	public final SortedSet<String> dependentModules;
	public final SortedMap<String, String> externalDependencies;
	public final File resourcesDirectory;

	public ModuleBundleParameters(String name, String definition, String version, ModuleFormat format, String sourceBaseUrl, String javaScript, String sourceMap, Collection<String> dependentModules, Map<String, String> externalDependencies, File resourcesDirectory) {
		this.name = name;
		this.definition = definition;
		this.version = version;
		this.format = format;
		this.sourceBaseUrl = sourceBaseUrl;
		this.javaScript = javaScript;
		this.sourceMap = sourceMap;
		this.dependentModules = ImmutableSortedSet.copyOf(dependentModules);
		this.externalDependencies = ImmutableSortedMap.copyOf(externalDependencies);
		this.resourcesDirectory = resourcesDirectory;
	}
}
