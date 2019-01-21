package com.prezi.spaghetti.packaging;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import com.prezi.spaghetti.bundle.ModuleBundleSet;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class ApplicationPackageParameters {
	public static final String DEFAULT_APPLICATION_NAME = "application.js";

	public final ModuleBundleSet bundles;
	public final ModuleBundleSet outOfDateBundlesForIncrementalTasks;
	public final String applicationName;
	public final String mainModule;
	public final boolean execute;
	public final List<String> prefixes;
	public final List<String> suffixes;
	public final SortedMap<String, String> externals;

	public ApplicationPackageParameters(ModuleBundleSet bundles, ModuleBundleSet outOfDateBundlesForIncrementalTasks, String applicationName, String mainModule, boolean execute, Iterable<String> prefixes, Iterable<String> suffixes, Map<String, String> externals) {
		this.bundles = bundles;
		this.outOfDateBundlesForIncrementalTasks = outOfDateBundlesForIncrementalTasks;
		this.applicationName = applicationName;
		this.mainModule = mainModule;
		this.execute = execute;
		this.prefixes = ImmutableList.copyOf(prefixes);
		this.suffixes = ImmutableList.copyOf(suffixes);
		this.externals = ImmutableSortedMap.copyOf(externals);
	}
}
