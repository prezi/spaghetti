package com.prezi.spaghetti.config.internal;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.config.ModuleConfiguration;

import java.util.SortedSet;
import java.util.TreeSet;

public class DefaultModuleConfiguration implements ModuleConfiguration {
	private final SortedSet<ModuleNode> localModules = new TreeSet<ModuleNode>();
	private final SortedSet<ModuleNode> directDependentModules = new TreeSet<ModuleNode>();
	private final SortedSet<ModuleNode> transitiveDependentModules = new TreeSet<ModuleNode>();

	public SortedSet<ModuleNode> getAllDependentModules() {
		return Sets.newTreeSet(Iterables.concat(directDependentModules, transitiveDependentModules));
	}

	public SortedSet<ModuleNode> getAllModules() {
		return Sets.newTreeSet(Iterables.concat(localModules, directDependentModules, transitiveDependentModules));
	}

	public SortedSet<ModuleNode> getLocalModules() {
		return localModules;
	}

	public SortedSet<ModuleNode> getDirectDependentModules() {
		return directDependentModules;
	}

	public SortedSet<ModuleNode> getTransitiveDependentModules() {
		return transitiveDependentModules;
	}
}
