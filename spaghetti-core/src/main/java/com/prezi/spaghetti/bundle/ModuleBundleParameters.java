package com.prezi.spaghetti.bundle;

import java.io.File;
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
	public final File resourcesDirectory;

	public ModuleBundleParameters(String name, String definition, String version, String sourceBaseUrl, String javaScript, String sourceMap, SortedSet<String> dependentModules, File resourcesDirectory) {
		this.name = name;
		this.definition = definition;
		this.version = version;
		this.sourceBaseUrl = sourceBaseUrl;
		this.javaScript = javaScript;
		this.sourceMap = sourceMap;
		this.dependentModules = dependentModules;
		this.resourcesDirectory = resourcesDirectory;
	}
}