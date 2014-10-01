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
	 * Returns dependencies of the local module.
	 */
	SortedSet<ModuleNode> getDependentModules();

	/**
	 * Returns all modules in the configuration, including the local module and its dependencies.
	 */
	SortedSet<ModuleNode> getAllModules();
}
