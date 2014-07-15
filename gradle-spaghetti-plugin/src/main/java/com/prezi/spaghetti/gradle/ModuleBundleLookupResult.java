package com.prezi.spaghetti.gradle;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.bundle.ModuleBundle;

import java.util.Set;

public class ModuleBundleLookupResult {
	private final Set<ModuleBundle> directBundles;
	private final Set<ModuleBundle> transitiveBundles;

	public ModuleBundleLookupResult(Set<ModuleBundle> directBundles, Set<ModuleBundle> transitiveBundles) {
		this.directBundles = directBundles;
		this.transitiveBundles = transitiveBundles;
	}

	public Set<ModuleBundle> getDirectBundles() {
		return directBundles;
	}

	public Set<ModuleBundle> getTransitiveBundles() {
		return transitiveBundles;
	}

	public Set<ModuleBundle> getAllBundles() {
		return Sets.newLinkedHashSet(Iterables.concat(directBundles, transitiveBundles));
	}
}
