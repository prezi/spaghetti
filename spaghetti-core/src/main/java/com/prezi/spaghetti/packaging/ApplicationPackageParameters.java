package com.prezi.spaghetti.packaging;

import com.prezi.spaghetti.bundle.ModuleBundleSet;

public class ApplicationPackageParameters {
	public static final String DEFAULT_APPLICATION_NAME = "application.js";

	public final ModuleBundleSet bundles;
	public final String applicationName;
	public final String mainModule;
	public final boolean execute;
	public final Iterable<String> prefixes;
	public final Iterable<String> suffixes;

	public ApplicationPackageParameters(ModuleBundleSet bundles, String applicationName, String mainModule, boolean execute, Iterable<String> prefixes, Iterable<String> suffixes) {
		this.bundles = bundles;
		this.applicationName = applicationName;
		this.mainModule = mainModule;
		this.execute = execute;
		this.prefixes = prefixes;
		this.suffixes = suffixes;
	}
}
