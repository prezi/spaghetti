package com.prezi.spaghetti.internal

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DependencyTreeResolver {
	private static final Logger logger = LoggerFactory.getLogger(DependencyTreeResolver)

	public static <M extends Comparable<M>, I> Map<M, I> resolveDependencies(Map<M, Collection<M>> dependencyTree, DependencyProcessor<M, I> processor) {
		Map<M, Set<M>> remainingModules = dependencyTree.collectEntries(new TreeMap<M, Set<M>>()) { module, dependencies ->
			[ module, dependencies.toSet() ]
		}

		// Check for non-existent modules
		remainingModules.each { module, dependencies ->
			dependencies.each {
				if (!remainingModules.containsKey(it)) {
					throw new IllegalArgumentException("Module found: ${it} (dependency of module ${module})")
				}
			}
		}

		Map<M, I> moduleInstances = [:]

		while (remainingModules) {
			// Find a module that has no more remaining dependencies
			M module = remainingModules.find { _, dependencies -> dependencies.size() == 0 }?.key
			if (!module) {
				throw new IllegalStateException("Cyclic dependency detected among modules: ${remainingModules.keySet()}")
			}
			// Remove it from among the modules we still need to load
			remainingModules.remove(module)
			def dependencies = dependencyTree.get(module).sort()

			logger.debug "Processing {} with dependencies: {}", module, dependencies

			// Look up previously loaded module
			def dependencyInstances = dependencies.collect { getInstance(moduleInstances, it) }
			def instance = processor.processDependency(module, dependencyInstances)
			moduleInstances.put(module, instance)

			// Remove module from among remaining dependencies of remaining other modules
			remainingModules.each { remainingModule, remainingDependencies ->
				remainingDependencies.remove(module)
			}
		}

		return moduleInstances
	}

	private static <M, I> I getInstance(Map<M, I> modules, M module) {
		if (!modules.containsKey(module)) {
			throw new IllegalStateException("Module not loaded: ${module}")
		}
		return modules.get(module)
	}

	public static interface DependencyProcessor<M, I> {
		I processDependency(M module, Collection<I> dependencies)
	}
}
