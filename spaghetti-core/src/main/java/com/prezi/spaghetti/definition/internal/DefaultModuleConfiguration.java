package com.prezi.spaghetti.definition.internal;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.definition.ModuleConfiguration;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class DefaultModuleConfiguration implements ModuleConfiguration {
	private ModuleNode localModule;
	private final SortedSet<ModuleNode> directDependentModules = new TreeSet<ModuleNode>();
	private final SortedSet<ModuleNode> transitiveDependentModules = new TreeSet<ModuleNode>();

	@Override
	public SortedSet<ModuleNode> getAllDependentModules() {
		return Sets.newTreeSet(Iterables.concat(directDependentModules, transitiveDependentModules));
	}

	@Override
	public SortedSet<ModuleNode> getAllModules() {
		return Sets.newTreeSet(Iterables.concat(Collections.singleton(localModule), directDependentModules, transitiveDependentModules));
	}

	@Override
	public ModuleNode getLocalModule() {
		return localModule;
	}

	public void setLocalModule(ModuleNode localModule) {
		this.localModule = localModule;
	}

	@Override
	public SortedSet<ModuleNode> getDirectDependentModules() {
		return directDependentModules;
	}

	@Override
	public SortedSet<ModuleNode> getTransitiveDependentModules() {
		return transitiveDependentModules;
	}
}
