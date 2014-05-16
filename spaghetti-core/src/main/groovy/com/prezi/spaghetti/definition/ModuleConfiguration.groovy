package com.prezi.spaghetti.definition
/**
 * Created by lptr on 15/11/13.
 */
class ModuleConfiguration {

	final Map<String, ModuleDefinition> modules
	final List<ModuleDefinition> localModules
	final Scope globalScope

	public ModuleConfiguration(Collection<ModuleDefinition> modules, Collection<ModuleDefinition> localModules, Scope globalScope) {
		this.modules = new TreeMap<String, ModuleDefinition>(modules.collectEntries { module -> [ (module.name): module ] }).asImmutable()
		this.localModules = localModules.sort().asImmutable()
		this.globalScope = globalScope
	}

	public List<ModuleDefinition> getDependentModules() {
		return modules.values().toList() - localModules
	}

	@Override
	String toString() {
		return "Dependent modules: " + dependentModules.collect { it.name }.join(", ") +
			", Local modules: " + localModules.collect { it.name }.join(", ")
	}
}
