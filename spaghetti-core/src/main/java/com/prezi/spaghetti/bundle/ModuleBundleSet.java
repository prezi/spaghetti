package com.prezi.spaghetti.bundle;

import com.google.common.base.Predicate;
import com.prezi.spaghetti.bundle.internal.DefaultModuleBundleSet;

import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;

public interface ModuleBundleSet extends SortedSet<ModuleBundle> {
	public static final ModuleBundleSet EMPTY = new DefaultModuleBundleSet(Collections.<ModuleBundle>emptySet(), Collections.<ModuleBundle>emptySet());

	SortedSet<ModuleBundle> getDirectBundles();
	SortedSet<ModuleBundle> getTransitiveBundles();
	Map<String, String> getExternalDependencies(Predicate<String> predicate);
	Map<String, String> getExternalDependencies();
}
