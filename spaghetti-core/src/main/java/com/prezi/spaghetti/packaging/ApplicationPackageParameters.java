package com.prezi.spaghetti.packaging;

import com.prezi.spaghetti.bundle.ModuleBundle;

import java.util.Set;

public class ApplicationPackageParameters {
	public static final String DEFAULT_APPLICATION_NAME = "application.js";

	public final Set<ModuleBundle> bundles;
	public final String applicationName;
	public final String mainModule;
	public final boolean execute;
	public final Iterable<String> prefixes;
	public final Iterable<String> suffixes;

	public ApplicationPackageParameters(Set<ModuleBundle> bundles, String applicationName, String mainModule, boolean execute, Iterable<String> prefixes, Iterable<String> suffixes) {
		this.bundles = bundles;
		this.applicationName = applicationName;
		this.mainModule = mainModule;
		this.execute = execute;
		this.prefixes = prefixes;
		this.suffixes = suffixes;
	}
}
