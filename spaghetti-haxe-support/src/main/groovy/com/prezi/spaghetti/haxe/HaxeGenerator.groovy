package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractGenerator
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
import groovy.text.SimpleTemplateEngine

/**
 * Created by lptr on 12/11/13.
 */
class HaxeGenerator extends AbstractGenerator {

	private final ModuleConfiguration config

	// Workaround variable to trick Haxe into exposing the module
	public static final String HAXE_MODULE_VAR = "__haxeModule"

	HaxeGenerator(ModuleConfiguration config) {
		this.config = config
	}

	@Override
	void generateHeaders(File outputDirectory) {
		config.localModules.each { module ->
			copySpaghettiClass(module, outputDirectory)
			generateModuleInterface(module, outputDirectory)
			generateModuleInitializer(module, outputDirectory)
			generateInterfacesForModuleTypes(module, outputDirectory, false)
		}
		config.dependentModules.each { dependentModule ->
			generateInterfacesForModuleTypes(dependentModule, outputDirectory, true)
			generateModuleProxy(dependentModule, outputDirectory)
		}
	}

	@Override
	String processModuleJavaScript(ModuleDefinition module, String javaScript)
	{
		return \
"""// Haxe expects either window or exports to be present
var exports = exports || {};
var ${HAXE_MODULE_VAR};
${javaScript}
return ${HAXE_MODULE_VAR};
"""
	}

	/**
	 * Copies Spaghetti.hx to the generated source directory.
	 */
	private static void copySpaghettiClass(ModuleDefinition module, File outputDirectory) {
		def template = new SimpleTemplateEngine().createTemplate(HaxeGenerator.class.getResource("/Spaghetti.hx"))
		new File(outputDirectory, "Spaghetti.hx") << template.make(moduleName: module.name)
	}

	/**
	 * Generates main interface for module.
	 */
	private static void generateModuleInterface(ModuleDefinition module, File outputDirectory)
	{
		def contents = new HaxeModuleInterfaceGeneratorVisitor(module).processModule()
		HaxeUtils.createHaxeSourceFile(module, module.alias, outputDirectory, contents)
	}

	/**
	 * Generates proxy for module.
	 */
	private static void generateModuleProxy(ModuleDefinition module, File outputDirectory)
	{
		def contents = new HaxeModuleProxyGeneratorVisitor(module).processModule()
		HaxeUtils.createHaxeSourceFile(module, module.alias, outputDirectory, contents)
	}

	/**
	 * Generates initializer for module.
	 */
	private static void generateModuleInitializer(ModuleDefinition module, File outputDirectory)
	{
		def initializerName = getInitializerName(module)
		def initializerContents = new HaxeModuleInitializerGeneratorVisitor(module).visitModuleDefinition(module.context)
		HaxeUtils.createHaxeSourceFile(module, initializerName, outputDirectory, initializerContents)
	}

	private static String getInitializerName(ModuleDefinition module) {
		return "__" + module.alias + "Init"
	}

	/**
	 * Generates interfaces the module should implement.
	 */
	private static void generateInterfacesForModuleTypes(ModuleDefinition module, File outputDirectory, boolean dependentModule)
	{
		new HaxeDefinitionIteratorVisitor(module, outputDirectory, dependentModule, {
			new HaxeInterfaceGeneratorVisitor(module)
		}).processModule()
	}
}
