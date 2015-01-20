package com.prezi.spaghetti.definition.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSortedSet;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.definition.ModuleConfiguration;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;

public class DefaultModuleConfiguration implements ModuleConfiguration {
	private final ModuleNode localModule;
	private final SortedSet<ModuleNode> directDependentModules;
	private final SortedSet<ModuleNode> transitiveDependentModules;
	private final SortedSet<ModuleNode> allDependentModules;
	private final SortedSet<ModuleNode> allModules;

	public DefaultModuleConfiguration(ModuleNode localModule, Set<ModuleNode> directDependentModules, Set<ModuleNode> transitiveDependentModules) {
		Preconditions.checkNotNull(localModule, "localModule");
		Preconditions.checkArgument(Collections.disjoint(
						Preconditions.checkNotNull(directDependentModules, "directDependentModules"),
						Preconditions.checkNotNull(transitiveDependentModules, "transitiveDependentModules")
				),
				"Some modules appear both to be direct and transitive dependencies, direct dependencies: " + directDependentModules
						+ ", transitive dependencies: " + transitiveDependentModules);
		Preconditions.checkArgument(!directDependentModules.contains(localModule),
				"Local module is also one of the direct dependencies, local module: " + localModule
						+ ", direct dependencies: " + directDependentModules);
		Preconditions.checkArgument(!transitiveDependentModules.contains(localModule),
				"Local module is also one of the transitive dependencies, local module: " + localModule
						+ ", transitive dependencies: " + transitiveDependentModules);

		this.localModule = localModule;
		this.directDependentModules = ImmutableSortedSet.copyOf(directDependentModules);
		this.transitiveDependentModules = ImmutableSortedSet.copyOf(transitiveDependentModules);
		this.allDependentModules = ImmutableSortedSet.<ModuleNode>naturalOrder().addAll(directDependentModules).addAll(transitiveDependentModules).build();
		this.allModules = ImmutableSortedSet.<ModuleNode>naturalOrder().add(localModule).addAll(directDependentModules).addAll(transitiveDependentModules).build();
	}

	@Override
	public ModuleNode getLocalModule() {
		return localModule;
	}

	@Override
	public SortedSet<ModuleNode> getDirectDependentModules() {
		return directDependentModules;
	}

	@Override
	public SortedSet<ModuleNode> getTransitiveDependentModules() {
		return transitiveDependentModules;
	}

	@Override
	public SortedSet<ModuleNode> getAllDependentModules() {
		return allDependentModules;
	}

	@Override
	public SortedSet<ModuleNode> getAllModules() {
		return allModules;
	}
}
