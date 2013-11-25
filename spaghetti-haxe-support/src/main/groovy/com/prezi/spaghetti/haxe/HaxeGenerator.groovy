package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition

/**
 * Created by lptr on 12/11/13.
 */
class HaxeGenerator implements Generator {

	private final ModuleConfiguration config

	HaxeGenerator(ModuleConfiguration config) {
		this.config = config
	}

	@Override
	void generateModuleHeaders(ModuleDefinition module, File outputDirectory)
	{
		generateModuleInterface(module, outputDirectory)
		generateModuleInitializer(module, outputDirectory)
		generateInterfacesForModuleInterfaces(module, outputDirectory)
		generateEnumClasses(module, outputDirectory)

		def modulesClassName = module.name.qualifyLocalName(FQName.fromString("Modules"))
		generateStuffForDependentModules(modulesClassName, outputDirectory)
	}

	@Override
	void generateApplication(String namespace, File outputDirectory)
	{
		def modulesClassName = FQName.fromString("${namespace}.Modules")
		generateStuffForDependentModules(modulesClassName, outputDirectory)
	}

	@Override
	String processModuleJavaScript(ModuleDefinition module, String javaScript)
	{
		return \
"""var __module;
${javaScript}
return __module;
"""
	}

	@Override
	String processApplicationJavaScript(String javaScript)
	{
		return javaScript
	}

	private void generateStuffForDependentModules(FQName modulesClassName, File outputDirectory) {
		config.dependentModules.each { dependentModule ->
			generateStructuralTypesForModuleInterfaces(dependentModule, outputDirectory)
			generateEnumClasses(dependentModule, outputDirectory)
		}
		generateClassToAccessDependentModules(modulesClassName, outputDirectory)
	}

	/**
	 * Generates main interface for module.
	 */
	private void generateModuleInterface(ModuleDefinition module, File outputDirectory)
	{
		def contents = new HaxeModuleGeneratorVisitor(
				config, module, { moduleName -> "interface ${moduleName} {" }
		).processModule()
		HaxeUtils.createHaxeSourceFile(module.name, outputDirectory, contents)
	}

	/**
	 * Generates initializer for module.
	 */
	private static void generateModuleInitializer(ModuleDefinition module, File outputDirectory)
	{
		def initializerName = "__" + module.name.localName + "Init"
		def initializerContents =
"""class ${initializerName} {
	public static function __init__() {
		var module:${module.name.localName} = new ${module.name.localName}Impl();
		untyped __module = module;
	}
}
"""
		HaxeUtils.createHaxeSourceFile(initializerName, module.name, outputDirectory, initializerContents)
	}

	/**
	 * Generates interfaces the module should implement.
	 */
	private void generateInterfacesForModuleInterfaces(ModuleDefinition module, File outputDirectory)
	{
		new HaxeTypeIteratorVisitor(config, module, outputDirectory, {
			new HaxeInterfaceGeneratorVisitor(config, module, { String typeName, FQName superType ->
				def declaration = "interface ${typeName}"
				if (superType != null)
				{
					declaration += " extends ${superType.fullyQualifiedName}"
				}
				declaration += " {"
				return declaration
			})
		}).processModule()
	}

	/**
	 * Generates typedefs (typedef <Enum> = Int;) and value classes for enums.
	 */
	private void generateEnumClasses(ModuleDefinition module, File outputDirectory)
	{
		new HaxeEnumIteratorVisitor(config, module, outputDirectory, {
			new HaxeEnumGeneratorVisitor()
		}).processModule()
	}

	/**
	 * Generates Modules.hx with methods like <code>get<ModuleName>():<ModuleName> { ... }</code>.
	 */
	private void generateClassToAccessDependentModules(FQName modulesClassName, File outputDirectory)
	{
		def dependentModules = config.dependentModules

		// Generate Modules.hx to access dependent modules
		if (!dependentModules.empty)
		{
			String modulesContents =
"""class ${modulesClassName.localName} {

	static var modules:Array<Dynamic>;

	static function __init__() {
		modules = untyped __modules;
	}
"""
			dependentModules.eachWithIndex { module, index ->
				modulesContents +=
"""
	public static inline function get${module.name.localName}():${module.name} {
		return modules[${index}];
	}
"""
			}
			modulesContents +=
"""}
"""
			HaxeUtils.createHaxeSourceFile(modulesClassName, outputDirectory, modulesContents)
		}
	}

	private void generateStructuralTypesForModuleInterfaces(ModuleDefinition module, File outputDirectory)
	{
		def moduleFileContents = new HaxeModuleGeneratorVisitor(
				config, module, { moduleName -> "typedef ${moduleName} = {" }
		).processModule()
		HaxeUtils.createHaxeSourceFile(module.name, outputDirectory, moduleFileContents)

		new HaxeTypeIteratorVisitor(config, module, outputDirectory, {
			new HaxeInterfaceGeneratorVisitor(config, module, { String typeName, FQName superType ->
				def declaration = "typedef ${typeName} = {"
				if (superType != null)
				{
					declaration += " > ${superType.fullyQualifiedName},"
				}
				return declaration
			})
		}).processModule()
	}
}

