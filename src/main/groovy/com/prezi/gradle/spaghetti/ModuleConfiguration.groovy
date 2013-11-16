package com.prezi.gradle.spaghetti
/**
 * Created by lptr on 15/11/13.
 */
class ModuleConfiguration {
	public static final def TYPE_VOID = FQName.fromString("void")
	public static final def TYPE_BOOL = FQName.fromString("bool")
	public static final def TYPE_INT = FQName.fromString("int")
	public static final def TYPE_FLOAT = FQName.fromString("float")
	public static final def TYPE_STRING = FQName.fromString("String")

	public static final Set<FQName> BUILT_IN_TYPE_NAMES = [
			TYPE_VOID, TYPE_BOOL, TYPE_INT, TYPE_FLOAT, TYPE_STRING
	]

	final Map<FQName, ModuleDefinition> modules

	final Set<FQName> typeNames

	public ModuleConfiguration(Collection<ModuleDefinition> modules, Set<FQName> typeNames) {
		this.modules = modules.collectEntries { module -> [ (module.name): module ] }
		this.typeNames = typeNames
	}

	public FQName resolveTypeName(String typeName, FQName moduleName) {
		def builtInTypeName = FQName.fromString(typeName)
		if (typeNames.contains(builtInTypeName)) {
			return builtInTypeName
		}
		def fqName = moduleName.resolveLocalName(typeName)
		if (!typeNames.contains(fqName)) {
			throw new IllegalStateException("Type does not exist: ${fqName}")
		}
		return fqName
	}
}
