package com.prezi.spaghetti.config

import com.prezi.spaghetti.ast.ModuleNode

/**
 * Created by lptr on 30/05/14.
 */
interface ModuleConfiguration {
	SortedSet<ModuleNode> getLocalModules()
	SortedSet<ModuleNode> getDirectDependentModules()
	SortedSet<ModuleNode> getTransitiveDependentModules()
	SortedSet<ModuleNode> getAllDependentModules()
	SortedSet<ModuleNode> getAllModules()
}
