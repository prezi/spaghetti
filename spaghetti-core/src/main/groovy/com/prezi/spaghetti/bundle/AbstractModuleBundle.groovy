package com.prezi.spaghetti.bundle

import groovy.transform.Canonical

@Canonical(includes = "name")
abstract class AbstractModuleBundle implements ModuleBundle {
	final String name
	final String version
	final String sourceBaseUrl
	final Set<String> dependentModules
	final Set<String> resourcePaths

	AbstractModuleBundle(String name, String version, String sourceBaseUrl, Set<String> dependentModules, Set<String> resourcePaths) {
		this.name = name
		this.version = version
		this.sourceBaseUrl = sourceBaseUrl
		this.dependentModules = dependentModules
		this.resourcePaths = resourcePaths
	}

	@Override
	int compareTo(ModuleBundle o) {
		return name.compareTo(o.name)
	}
}
