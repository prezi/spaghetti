package com.prezi.spaghetti.bundle.internal;

import com.google.common.collect.ImmutableSortedSet;
import com.prezi.spaghetti.bundle.ModuleBundle;

import java.util.Set;
import java.util.SortedSet;

public abstract class AbstractModuleBundle implements ModuleBundleInternal {
	private final String name;
	private final String version;
	private final String sourceBaseUrl;
	private final SortedSet<String> dependentModules;
	private final SortedSet<String> externalDependencies;
	private final SortedSet<String> resourcePaths;

	public AbstractModuleBundle(String name, String version, String sourceBaseUrl, Set<String> dependentModules, Set<String> externalDependencies, Set<String> resourcePaths) {
		this.name = name;
		this.version = version;
		this.sourceBaseUrl = sourceBaseUrl;
		this.dependentModules = ImmutableSortedSet.copyOf(dependentModules);
		this.externalDependencies = ImmutableSortedSet.copyOf(externalDependencies);
		this.resourcePaths = ImmutableSortedSet.copyOf(resourcePaths);
	}

	@Override
	public int compareTo(ModuleBundle o) {
		return name.compareTo(o.getName());
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (!getClass().equals(o.getClass())) return false;

		AbstractModuleBundle that = (AbstractModuleBundle) o;

		return name.equals(that.name);
	}

	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final String getVersion() {
		return version;
	}

	@Override
	public final String getSourceBaseUrl() {
		return sourceBaseUrl;
	}

	@Override
	public final SortedSet<String> getDependentModules() {
		return dependentModules;
	}

	@Override
	public final SortedSet<String> getExternalDependencies() {
		return externalDependencies;
	}

	@Override
	public final SortedSet<String> getResourcePaths() {
		return resourcePaths;
	}
}
