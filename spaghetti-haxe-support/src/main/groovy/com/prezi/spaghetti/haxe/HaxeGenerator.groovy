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

		generateStuffForDependentModules(outputDirectory)
	}

	@Override
	void generateApplication(String namespace, File outputDirectory)
	{
		generateStuffForDependentModules(outputDirectory)
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

	private void generateStuffForDependentModules(File outputDirectory) {
		config.dependentModules.eachWithIndex { dependentModule, index ->
			generateStructuralTypesForModuleTypes(dependentModule, outputDirectory)
			generateModuleProxy(dependentModule, index, outputDirectory)
		}
	}

	/**
	 * Generates main interface for module.
	 */
	private static void generateModuleInterface(ModuleDefinition module, File outputDirectory)
	{
		def contents = new HaxeModuleInterfaceGeneratorVisitor(module).processModule()
		HaxeUtils.createHaxeSourceFile(module.name, outputDirectory, contents)
	}

	/**
	 * Generates proxy for module.
	 */
	private static void generateModuleProxy(ModuleDefinition module, int moduleIndex, File outputDirectory)
	{
		def contents = new HaxeModuleProxyGeneratorVisitor(module, moduleIndex).processModule()
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
			new HaxeInterfaceGeneratorVisitor(module, { String typeName, String superType ->
				def declaration = "interface ${typeName}"
				if (superType != null)
				{
					declaration += " extends ${superType}"
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
		new HaxeDefinitionIteratorVisitor(module, outputDirectory, {
			new HaxeInterfaceGeneratorVisitor(module, { String typeName, String superType ->
				def declaration = "extern class ${typeName} "
				if (superType != null)
				{
					declaration += "extends ${superType} "
				}
				return declaration + "{"
			})
		}).processModule()
	}
}
