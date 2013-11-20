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
		generateInterfacesForModuleTypes(config, module, outputDirectory)

		def modulesClassName = module.name.resolveLocalName(FQName.fromString("Modules"))
		generateStuffForDependentModules(config, modulesClassName, outputDirectory)
	}

	@Override
	void generateApplication(ModuleConfiguration config, String namespace, File outputDirectory)
	{
		def modulesClassName = FQName.fromString("${namespace}.Modules")
		generateStuffForDependentModules(config, modulesClassName, outputDirectory)
	}

	private static void generateStuffForDependentModules(ModuleConfiguration config, FQName modulesClassName, File outputDirectory) {
		generateStructuralTypesForDependentModuleTypes(config, outputDirectory)
		generateClassToAccessDependentModules(config, modulesClassName, outputDirectory)
	}

	/**
	 * Generates main interface for module.
	 */
	private static void generateModuleInterface(ModuleDefinition module, File outputDirectory, ModuleConfiguration config)
	{
		HaxeUtils.createHaxeSourceFile(module.name, outputDirectory,
				new HaxeModuleGeneratorVisitor(
						config, module, { moduleName -> "interface ${moduleName} {" }
				).processModule()
		)
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
	private static void generateInterfacesForModuleTypes(ModuleConfiguration config, ModuleDefinition module, File outputDirectory)
	{
		new HaxeTypeIteratorVisitor(module, outputDirectory, {
			new HaxeTypeGeneratorVisitor(config, module, { String typeName, FQName superType ->
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

	private
	static void generateClassToAccessDependentModules(ModuleConfiguration config, FQName modulesClassName, File outputDirectory)
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

	private static void generateStructuralTypesForDependentModuleTypes(ModuleConfiguration config, File outputDirectory)
	{
		config.dependentModules.each { dependentModule ->
			HaxeUtils.createHaxeSourceFile(dependentModule.name, outputDirectory,
					new HaxeModuleGeneratorVisitor(
							config, dependentModule, { moduleName -> "typedef ${moduleName} = {" }
					).processModule()
			)
			new HaxeTypeIteratorVisitor(dependentModule, outputDirectory, {
				new HaxeTypeGeneratorVisitor(config, dependentModule, { String typeName, FQName superType ->
					def declaration = "typedef ${typeName} = {"
					if (superType != null)
					{
						declaration += " > ${superType.fullyQualifiedName},"
					}
					return declaration
				})
			}).visit(dependentModule.context)
		}
	}

	@Override
	String processModuleJavaScript(ModuleConfiguration config, ModuleDefinition module, String javaScript)
	{
		return "var __module;\n" + javaScript + "return __module;\n"
	}
}
