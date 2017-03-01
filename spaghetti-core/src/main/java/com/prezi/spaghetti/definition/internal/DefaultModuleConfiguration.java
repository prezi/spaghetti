package com.prezi.spaghetti.definition.internal;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSortedSet;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.definition.EntityWithModuleMetaData;
import com.prezi.spaghetti.definition.ModuleConfiguration;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;

public class DefaultModuleConfiguration implements ModuleConfiguration {
	private final ModuleNode localModule;
	private final SortedSet<EntityWithModuleMetaData<ModuleNode>> directDependentModules;
	private final SortedSet<EntityWithModuleMetaData<ModuleNode>> transitiveDependentModules;
	private final SortedSet<ModuleNode> allDependentModules;
	private final SortedSet<ModuleNode> allModules;

	public DefaultModuleConfiguration(ModuleNode localModule, Set<EntityWithModuleMetaData<ModuleNode>> directDependentModules, Set<EntityWithModuleMetaData<ModuleNode>> transitiveDependentModules) {
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
		Comparator<EntityWithModuleMetaData<ModuleNode>> moduleNodeEntityComparator = DefaultEntityWithModuleMetaData.<ModuleNode>mkComparator();
		this.directDependentModules = ImmutableSortedSet.copyOf(moduleNodeEntityComparator, directDependentModules);
		this.transitiveDependentModules = ImmutableSortedSet.copyOf(moduleNodeEntityComparator, transitiveDependentModules);
		this.allDependentModules = ImmutableSortedSet.<ModuleNode>naturalOrder().addAll(stripMetaData(directDependentModules)).addAll(stripMetaData(transitiveDependentModules)).build();
		this.allModules = ImmutableSortedSet.<ModuleNode>naturalOrder().add(localModule).addAll(stripMetaData(directDependentModules)).addAll(stripMetaData(transitiveDependentModules)).build();
	}

	private <T> Collection<T> stripMetaData(Collection<EntityWithModuleMetaData<T>> nodes) {
		return Collections2.transform(nodes, new Function<EntityWithModuleMetaData<T>, T>() {
			@Nullable
			@Override
			public T apply(EntityWithModuleMetaData<T> input) {
				return input.getEntity();
			}
		});
	}

	@Override
	public ModuleNode getLocalModule() {
		return localModule;
	}

	@Override
	public SortedSet<EntityWithModuleMetaData<ModuleNode>> getDirectDependentModules() {
		return directDependentModules;
	}

	@Override
	public SortedSet<EntityWithModuleMetaData<ModuleNode>> getTransitiveDependentModules() {
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

	@Override
	public String toString() {
		return "DefaultModuleConfiguration("
				+ "local module: " + localModule
				+ ", direct dependencies: " + directDependentModules
				+ ", transitive dependencies: " + transitiveDependentModules
				+ ")";
	}
}
