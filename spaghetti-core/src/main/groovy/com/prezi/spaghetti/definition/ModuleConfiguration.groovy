package com.prezi.spaghetti.definition
/**
 * Created by lptr on 15/11/13.
 */
class ModuleConfiguration {

	final List<ModuleDefinition> dependentModules
	final List<ModuleDefinition> localModules
	final Scope globalScope

	public ModuleConfiguration(Collection<ModuleDefinition> dependentModules, Collection<ModuleDefinition> localModules, Scope globalScope) {
		this.dependentModules = dependentModules.sort().asImmutable()
		this.localModules = localModules.sort().asImmutable()
		this.globalScope = globalScope
	}

	public List<ModuleDefinition> getDynamicDependentModules() {
		return dependentModules.findAll { it.dynamic }
	}

	public List<ModuleDefinition> getStaticDependentModules() {
		return dependentModules.findAll { !it.dynamic }
	}

	@Override
	String toString() {
		return "Dependent modules: " + dependentModules.collect { it.name }.join(", ") +
			", Local modules: " + localModules.collect { it.name }.join(", ")
	}
}
