package com.prezi.spaghetti.config

import com.prezi.spaghetti.ast.ModuleNode

interface ModuleConfiguration {
	SortedSet<ModuleNode> getLocalModules()
	SortedSet<ModuleNode> getDirectDependentModules()
	SortedSet<ModuleNode> getTransitiveDependentModules()
	SortedSet<ModuleNode> getAllDependentModules()
	SortedSet<ModuleNode> getAllModules()
}
