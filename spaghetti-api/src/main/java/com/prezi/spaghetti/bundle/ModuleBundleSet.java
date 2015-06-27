package com.prezi.spaghetti.bundle;

import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public interface ModuleBundleSet extends SortedSet<ModuleBundle> {
	SortedSet<ModuleBundle> getDirectBundles();
	SortedSet<ModuleBundle> getTransitiveBundles();
	SortedMap<String, Set<String>> getExternalDependencies();
}
