package com.prezi.spaghetti.definition;

import com.prezi.spaghetti.ast.ModuleNode;

import java.util.SortedSet;

/**
 * Stores the configuration of a parsed Spaghetti module and its dependencies.
 */
public interface ModuleConfiguration {
	/**
	 * Returns the local module.
	 */
	ModuleNode getLocalModule();

	/**
	 * Returns all direct dependencies of the local module.
	 */
	SortedSet<ModuleNode> getDirectDependentModules();

	/**
	 * Returns all transitive dependencies of the local module.
	 */
	SortedSet<ModuleNode> getTransitiveDependentModules();

	/**
	 * Returns all dependencies of the local module, including both direct and transitive dependencies.
	 */
	SortedSet<ModuleNode> getAllDependentModules();

	/**
	 * Returns all modules in the configuration, including the local module, and its direct and transitive dependencies.
	 */
	SortedSet<ModuleNode> getAllModules();
}
