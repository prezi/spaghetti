package com.prezi.spaghetti.packaging;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.prezi.spaghetti.bundle.ModuleBundleSet;

import java.util.Map;

public class ApplicationPackageParameters {
	public static final String DEFAULT_APPLICATION_NAME = "application.js";

	public final ModuleBundleSet bundles;
	public final String applicationName;
	public final String mainModule;
	public final boolean execute;
	public final Iterable<String> prefixes;
	public final Iterable<String> suffixes;
	public final Map<String, String> externals;

	public ApplicationPackageParameters(ModuleBundleSet bundles, String applicationName, String mainModule, boolean execute, Iterable<String> prefixes, Iterable<String> suffixes, Map<String, String> externals) {
		this.bundles = bundles;
		this.applicationName = applicationName;
		this.mainModule = mainModule;
		this.execute = execute;
		this.prefixes = ImmutableSortedSet.copyOf(prefixes);
		this.suffixes = ImmutableSortedSet.copyOf(suffixes);
		this.externals = ImmutableMap.copyOf(externals);
	}
}
