package com.prezi.spaghetti.bundle.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingSortedSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public class DefaultModuleBundleSet extends ForwardingSortedSet<ModuleBundle> implements ModuleBundleSet {
	private final SortedSet<ModuleBundle> directBundles;
	private final SortedSet<ModuleBundle> lazyBundles;
	private final SortedSet<ModuleBundle> transitiveBundles;
	private final SortedSet<ModuleBundle> allBundles;

	public DefaultModuleBundleSet(Collection<ModuleBundle> directBundles, Collection<ModuleBundle> lazyBundles, Collection<ModuleBundle> transitiveBundles) {
		Preconditions.checkArgument(Collections.disjoint(
						Preconditions.checkNotNull(directBundles, "directBundles"),
						Preconditions.checkNotNull(transitiveBundles, "transitiveBundles")
				),
				"Some bundles are both direct and transitive, direct bundles: " + directBundles
						+ ", transitive bundles: " + transitiveBundles);
		this.directBundles = ImmutableSortedSet.copyOf(directBundles);
		this.lazyBundles = ImmutableSortedSet.copyOf(lazyBundles);
		this.transitiveBundles = ImmutableSortedSet.copyOf(transitiveBundles);
		this.allBundles = ImmutableSortedSet.<ModuleBundle>naturalOrder().addAll(directBundles).addAll(lazyBundles).addAll(transitiveBundles).build();
	}

	@Override
	protected SortedSet<ModuleBundle> delegate() {
		return allBundles;
	}

	@Override
	public SortedSet<ModuleBundle> getDirectBundles() {
		return directBundles;
	}

	@Override
	public SortedSet<ModuleBundle> getLazyBundles() {
		return lazyBundles;
	}

	@Override
	public SortedSet<ModuleBundle> getTransitiveBundles() {
		return transitiveBundles;
	}

	@Override
	public SortedMap<String, Set<String>> getExternalDependencies() {
		SortedMap<String, Set<String>> externals = Maps.newTreeMap();
		for (ModuleBundle bundle : this) {
			for (String external : bundle.getExternalDependencies().values()) {
				Set<String> bundles = externals.containsKey(external) ?
						externals.get(external) :
						Sets.<String>newTreeSet();
				bundles.add(bundle.getName());
				externals.put(external, bundles);
			}
		}
		return externals;
	}
}
