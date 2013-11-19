package com.prezi.spaghetti
/**
 * Created by lptr on 15/11/13.
 */
class ModuleConfiguration {
	public static final def TYPE_VOID = FQName.fromString("void")
	public static final def TYPE_BOOL = FQName.fromString("bool")
	public static final def TYPE_INT = FQName.fromString("int")
	public static final def TYPE_FLOAT = FQName.fromString("float")
	public static final def TYPE_STRING = FQName.fromString("String")
	public static final def TYPE_ANY = FQName.fromString("any")

	public static final Set<FQName> BUILT_IN_TYPE_NAMES = [
			TYPE_VOID, TYPE_BOOL, TYPE_INT, TYPE_FLOAT, TYPE_STRING, TYPE_ANY
	]

	final Map<FQName, ModuleDefinition> modules
	final List<ModuleDefinition> localModules
	final Set<FQName> typeNames

	public ModuleConfiguration(Collection<ModuleDefinition> modules, Collection<ModuleDefinition> localModules, Set<FQName> typeNames) {
		this.modules = modules.collectEntries { module -> [ (module.name): module ] }
		this.localModules = localModules
		this.typeNames = typeNames
	}

	public List<ModuleDefinition> getDependentModules() {
		return modules.values().toList() - localModules
	}

	public FQName resolveTypeName(FQName typeName, FQName moduleName) {
		if (typeNames.contains(typeName)) {
			return typeName
		}
		def fqName = moduleName.resolveLocalName(typeName)
		if (!typeNames.contains(fqName)) {
			throw new IllegalStateException("Type does not exist: ${fqName}")
		}
		return fqName
	}

	@Override
	String toString() {
		return "modules: " + modules.values().collect { it.name }.join(", ")
	}
}
