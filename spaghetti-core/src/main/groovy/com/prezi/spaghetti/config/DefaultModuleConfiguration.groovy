package com.prezi.spaghetti.config

import com.prezi.spaghetti.ast.ModuleNode

/**
 * Created by lptr on 30/05/14.
 */
class DefaultModuleConfiguration implements ModuleConfiguration {
	final SortedSet<ModuleNode> localModules = new TreeSet<>()
	final SortedSet<ModuleNode> directDependentModules = new TreeSet<>()
	final SortedSet<ModuleNode> transitiveDependentModules = new TreeSet<>()

	SortedSet<ModuleNode> getAllDependentModules() {
		return new TreeSet<>(directDependentModules + transitiveDependentModules)
	}

	SortedSet<ModuleNode> getAllModules() {
		return new TreeSet<>(localModules + directDependentModules + transitiveDependentModules)
	}
}
