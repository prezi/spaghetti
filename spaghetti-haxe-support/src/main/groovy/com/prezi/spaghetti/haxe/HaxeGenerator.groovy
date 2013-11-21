package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
/**
 * Created by lptr on 12/11/13.
 */
class HaxeGenerator extends Generator {

	HaxeGenerator() {
		super("haxe")
	}

	@Override
	void generateModuleHeaders(ModuleConfiguration config, ModuleDefinition module, File outputDirectory)
	{
		generateModuleInterface(module, outputDirectory, config)
		generateModuleInitializer(module, outputDirectory)
		generateInterfacesForModuleInterfaces(config, module, outputDirectory)
		generateEnumClasses(module, outputDirectory)

		def modulesClassName = module.name.resolveLocalName(FQName.fromString("Modules"))
		generateStuffForDependentModules(config, modulesClassName, outputDirectory)
	}

	@Override
	void generateApplication(ModuleConfiguration config, String namespace, File outputDirectory)
	{
		def modulesClassName = FQName.fromString("${namespace}.Modules")
		generateStuffForDependentModules(config, modulesClassName, outputDirectory)
	}

	@Override
	String processModuleJavaScript(ModuleConfiguration config, ModuleDefinition module, String javaScript)
	{
		return "var __module;\n" + javaScript + "return __module;\n"
	}

	private static void generateStuffForDependentModules(ModuleConfiguration config, FQName modulesClassName, File outputDirectory) {
		config.dependentModules.each { dependentModule ->
			generateStructuralTypesForModuleInterfaces(config, dependentModule, outputDirectory)
			generateEnumClasses(dependentModule, outputDirectory)
		}
		generateClassToAccessDependentModules(config, modulesClassName, outputDirectory)
	}

	/**
	 * Generates main interface for module.
	 */
	private static void generateModuleInterface(ModuleDefinition module, File outputDirectory, ModuleConfiguration config)
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
		def initializerContents = """class ${initializerName} {
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
	private static void generateInterfacesForModuleInterfaces(ModuleConfiguration config, ModuleDefinition module, File outputDirectory)
	{
		new HaxeTypeIteratorVisitor(module, outputDirectory, {
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
	private static void generateEnumClasses(ModuleDefinition module, File outputDirectory)
	{
		new HaxeEnumIteratorVisitor(module, outputDirectory, {
			new HaxeEnumGeneratorVisitor()
		}).processModule()
	}

	/**
	 * Generates Modules.hx with methods like <code>get<ModuleName>():<ModuleName> { ... }</code>.
	 */
	private static void generateClassToAccessDependentModules(ModuleConfiguration config, FQName modulesClassName, File outputDirectory)
	{
		def dependentModules = config.dependentModules

		// Generate Modules.hx to access dependent modules
		if (!dependentModules.empty)
		{
			String modulesContents = """class ${modulesClassName.localName} {

	static var modules:Array<Dynamic>;

	static function __init__() {
		modules = untyped __modules;
	}
"""
			dependentModules.eachWithIndex { module, index ->
				modulesContents += """
	public static inline function get${module.name.localName}():${module.name} {
		return modules[${index}];
	}
"""
			}
			modulesContents += "}\n"
			HaxeUtils.createHaxeSourceFile(modulesClassName, outputDirectory, modulesContents)
		}
	}

	private static void generateStructuralTypesForModuleInterfaces(ModuleConfiguration config, ModuleDefinition module, File outputDirectory)
	{
		def moduleFileContents = new HaxeModuleGeneratorVisitor(
				config, module, { moduleName -> "typedef ${moduleName} = {" }
		).processModule()
		HaxeUtils.createHaxeSourceFile(module.name, outputDirectory, moduleFileContents)

		new HaxeTypeIteratorVisitor(module, outputDirectory, {
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
