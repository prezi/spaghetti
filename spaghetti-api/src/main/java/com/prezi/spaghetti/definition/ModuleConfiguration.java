package com.prezi.spaghetti.definition;

import com.prezi.spaghetti.ast.ModuleNode;

import java.util.SortedSet;

/**
 * Stores the configuration of a parsed Spaghetti module and its dependencies.
 */
public interface ModuleConfiguration {
	/**
	 * Returns the local module.
	 *
	 * @return the local module.
	 */
	ModuleNode getLocalModule();

	/**
	 * Returns direct dependencies of the local module.
	 *
	 * @return direct dependencies of the local module.
	 */
	SortedSet<EntityWithModuleMetaData<ModuleNode>> getDirectDependentModules();

	/**
	 * Returns transitive dependencies of the local module.
	 *
	 * @return transitive dependencies of the local module.
	 */
	SortedSet<EntityWithModuleMetaData<ModuleNode>> getTransitiveDependentModules();

	/**
	 * Returns all dependencies of the local module, including both direct and transitive dependencies.
	 *
	 * @return all dependencies of the local module.
	 */
	SortedSet<ModuleNode> getAllDependentModules();

	/**
	 * Returns all modules in the configuration, including the local module and its dependencies.
	 */
	SortedSet<ModuleNode> getAllModules();
}
