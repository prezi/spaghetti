package com.prezi.spaghetti.bundle;

import java.util.Set;

public abstract class AbstractModuleBundle implements ModuleBundle {
	private final String name;
	private final String version;
	private final String sourceBaseUrl;
	private final Set<String> dependentModules;
	private final Set<String> resourcePaths;

	public AbstractModuleBundle(String name, String version, String sourceBaseUrl, Set<String> dependentModules, Set<String> resourcePaths) {
		this.name = name;
		this.version = version;
		this.sourceBaseUrl = sourceBaseUrl;
		this.dependentModules = dependentModules;
		this.resourcePaths = resourcePaths;
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

	public final String getName() {
		return name;
	}

	public final String getVersion() {
		return version;
	}

	public final String getSourceBaseUrl() {
		return sourceBaseUrl;
	}

	public final Set<String> getDependentModules() {
		return dependentModules;
	}

	public final Set<String> getResourcePaths() {
		return resourcePaths;
	}
}
