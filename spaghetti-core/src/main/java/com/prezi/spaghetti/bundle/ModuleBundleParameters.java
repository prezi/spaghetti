package com.prezi.spaghetti.bundle;

import com.google.common.collect.ImmutableSortedSet;

import java.io.File;
import java.util.Collection;
import java.util.SortedSet;

/**
 * Parameters for creating module bundles.
 */
public class ModuleBundleParameters {
	public final String name;
	public final String definition;
	public final String version;
	public final String sourceBaseUrl;
	public final String javaScript;
	public final String sourceMap;
	public final SortedSet<String> dependentModules;
	public final SortedSet<String> externalDependencies;
	public final File resourcesDirectory;

	public ModuleBundleParameters(String name, String definition, String version, String sourceBaseUrl, String javaScript, String sourceMap, Collection<String> dependentModules, Collection<String> externalDependencies, File resourcesDirectory) {
		this.name = name;
		this.definition = definition;
		this.version = version;
		this.sourceBaseUrl = sourceBaseUrl;
		this.javaScript = javaScript;
		this.sourceMap = sourceMap;
		this.dependentModules = ImmutableSortedSet.copyOf(dependentModules);
		this.externalDependencies = ImmutableSortedSet.copyOf(externalDependencies);
		this.resourcesDirectory = resourcesDirectory;
	}
}
