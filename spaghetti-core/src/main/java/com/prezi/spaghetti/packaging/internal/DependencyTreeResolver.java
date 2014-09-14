package com.prezi.spaghetti.packaging.internal;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DependencyTreeResolver {
	private static final Logger logger = LoggerFactory.getLogger(DependencyTreeResolver.class);

	public static <M extends Comparable<M>, I> Map<M, I> resolveDependencies(Map<M, ? extends Collection<M>> dependencyTree, DependencyProcessor<M, I> processor) {
		Map<M, Set<M>> remainingModules = Maps.newTreeMap();
		for (Map.Entry<M, ? extends Collection<M>> entry : dependencyTree.entrySet()) {
			remainingModules.put(entry.getKey(), Sets.newLinkedHashSet(entry.getValue()));
		}

		// Check for non-existent modules
		for (Map.Entry<M, Set<M>> entry : remainingModules.entrySet()) {
			for (M dependency : entry.getValue()) {
				if (!remainingModules.containsKey(dependency)) {
					throw new IllegalArgumentException("Module not found: " + dependency + " (dependency of module " + entry.getKey() + ")");
				}
			}
		}

		final Map<M, I> moduleInstances = Maps.newLinkedHashMap();

		while (!remainingModules.isEmpty()) {
			// Find a module that has no more remaining dependencies
			Map.Entry<M, Set<M>> entry = Iterables.find(remainingModules.entrySet(), new Predicate<Map.Entry<M, Set<M>>>() {
				@Override
				public boolean apply(Map.Entry<M, Set<M>> input) {
					return input.getValue().isEmpty();
				}
			}, null);
			if (entry == null) {
				throw new IllegalStateException("Cyclic dependency detected among modules: " + remainingModules.keySet());
			}
			M module = entry.getKey();
			// Remove it from among the modules we still need to load
			remainingModules.remove(module);
			List<M> dependencies = Lists.newArrayList(dependencyTree.get(module));
			Collections.sort(dependencies, Ordering.natural());

			logger.debug("Processing {} with dependencies: {}", module, dependencies);

			// Look up previously loaded module
			Collection<I> dependencyInstances = Lists.newArrayList(Collections2.transform(dependencies, new Function<M, I>() {
				@Override
				public I apply(@Nullable M input) {
					return getInstance(moduleInstances, input);
				}
			}));
			I instance = processor.processDependency(module, dependencyInstances);
			moduleInstances.put(module, instance);

			// Remove module from among remaining dependencies of remaining other modules
			for (Set<M> remainingDependencies : remainingModules.values()) {
				remainingDependencies.remove(module);
			}
		}

		return moduleInstances;
	}

	private static <M, I> I getInstance(Map<M, I> modules, M module) {
		if (!modules.containsKey(module)) {
			throw new IllegalStateException("Module not loaded: " + module);
		}
		return modules.get(module);
	}

	public static interface DependencyProcessor<M, I> {
		I processDependency(M module, Collection<I> dependencies);
	}
}
