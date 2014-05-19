package com.prezi.spaghetti.definition
/**
 * Created by lptr on 15/11/13.
 */
class ModuleConfiguration {

	final Set<ModuleDefinition> localModules
	final Set<ModuleDefinition> directDependentModules
	final Set<ModuleDefinition> transitiveDependentModules
	final Scope globalScope

	public ModuleConfiguration(Collection<ModuleDefinition> localModules, Collection<ModuleDefinition> directDependentModules, Collection<ModuleDefinition> transitiveDependentModules, Scope globalScope) {
		this.localModules = new TreeSet<>(localModules).asImmutable()
		this.directDependentModules = new TreeSet<>(directDependentModules).asImmutable()
		this.transitiveDependentModules = new TreeSet<>(transitiveDependentModules).asImmutable()
		this.globalScope = globalScope
	}

	public SortedSet<ModuleDefinition> getDirectDynamicDependentModules() {
		return directDependentModules.findAll { it.dynamic }
	}

	public SortedSet<ModuleDefinition> getDirectStaticDependentModules() {
		return directDependentModules.findAll { !it.dynamic }
	}

	public SortedSet<ModuleDefinition> getAllDynamicDependentModules() {
		return allDependentModules.findAll { it.dynamic }
	}

	public SortedSet<ModuleDefinition> getAllStaticDependentModules() {
		return allDependentModules.findAll { !it.dynamic }
	}

	public SortedSet<ModuleDefinition> getAllDependentModules() {
		return new TreeSet<>(directDependentModules + transitiveDependentModules)
	}

	public List<ModuleDefinition> getAllDynamicModules() {
		return allModules.findAll { it.dynamic }
	}

	public List<ModuleDefinition> getAllStaticModules() {
		return allModules.findAll { !it.dynamic }
	}

	public List<ModuleDefinition> getAllModules() {
		return (localModules + directDependentModules + transitiveDependentModules)
	}

	@Override
	String toString() {
		return \
			"Local modules: " + localModules*.name.join(", ") +
		 	", Direct dependent modules: " + directDependentModules*.name.join(", ") +
		 	", Transitive dependent modules: " + transitiveDependentModules*.name.join(", ")
	}
}
