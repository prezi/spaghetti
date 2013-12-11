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
		generateInterfacesForModuleTypes(module, outputDirectory)

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
			generateStructuralTypesForModuleTypes(dependentModule, outputDirectory)
		}
		generateClassToAccessDependentModules(modulesClassName, outputDirectory)
	}

	/**
	 * Generates main interface for module.
	 */
	private static void generateModuleInterface(ModuleDefinition module, File outputDirectory)
	{
		def contents = new HaxeModuleGeneratorVisitor(
				module, { moduleName -> "interface ${moduleName} {" }
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
#if (js && !test)
	public static function __init__() {
		var module:${module.name.localName} = new ${module.name.localName}Impl();
		untyped __module = module;
	}
#end
}
"""
		HaxeUtils.createHaxeSourceFile(initializerName, module.name, outputDirectory, initializerContents)
	}

	/**
	 * Generates interfaces the module should implement.
	 */
	private static void generateInterfacesForModuleTypes(ModuleDefinition module, File outputDirectory)
	{
		new HaxeDefinitionIteratorVisitor(module, outputDirectory, {
			new HaxeInterfaceGeneratorVisitor(module, { String typeName, FQName superType ->
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
	 * Generates structural types on the caller side.
	 */
	private static void generateStructuralTypesForModuleTypes(ModuleDefinition module, File outputDirectory)
	{
		def moduleFileContents = new HaxeModuleGeneratorVisitor(
				module, { moduleName -> "extern class ${moduleName} {" }
		).processModule()
		HaxeUtils.createHaxeSourceFile(module.name, outputDirectory, moduleFileContents)

		new HaxeDefinitionIteratorVisitor(module, outputDirectory, {
			new HaxeInterfaceGeneratorVisitor(module, { String typeName, FQName superType ->
				def declaration = "extern class ${typeName} "
				if (superType != null)
				{
					declaration += "extends ${superType.fullyQualifiedName} "
				}
				return declaration + "{"
			})
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
#if js
"""
			dependentModules.eachWithIndex { module, index ->
				modulesContents +=
"""
	public static inline function get${module.name.localName}():${module.name} {
		return untyped __modules[${index}];
	}
"""
			}
			modulesContents +=
"""
#end
}
"""
			HaxeUtils.createHaxeSourceFile(modulesClassName, outputDirectory, modulesContents)
		}
	}
}
