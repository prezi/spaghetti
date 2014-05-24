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

	public SortedSet<ModuleDefinition> getAllDependentModules() {
		return new TreeSet<>(directDependentModules + transitiveDependentModules)
	}

	public SortedSet<ModuleDefinition> getAllModules() {
		return new TreeSet<>(localModules + directDependentModules + transitiveDependentModules)
	}

	@Override
	String toString() {
		return \
			"Local modules: " + localModules*.name.join(", ") +
		 	", Direct dependent modules: " + directDependentModules*.name.join(", ") +
		 	", Transitive dependent modules: " + transitiveDependentModules*.name.join(", ")
	}
}
