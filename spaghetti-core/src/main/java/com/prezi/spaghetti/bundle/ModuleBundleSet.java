package com.prezi.spaghetti.bundle;

import java.util.SortedSet;

public interface ModuleBundleSet extends SortedSet<ModuleBundle> {
	SortedSet<ModuleBundle> getDirectBundles();
	SortedSet<ModuleBundle> getTransitiveBundles();
}
