package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
import groovy.text.SimpleTemplateEngine

/**
 * Created by lptr on 12/11/13.
 */
class HaxeGenerator implements Generator {

	private final ModuleConfiguration config

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
		/* the "exports" line is needed because if this module is
		   requirejs'd from a nodejs module 'exports' will not be
		   visible for some reason
		*/
		return \
"""var __module; var exports = exports || {}; ${javaScript}
return __module;
"""
	}

	@Override
	String processApplicationJavaScript(String javaScript)
	{
		return javaScript
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
		def initializerName = "__" + module.alias + "Init"
		def initializerContents = new HaxeModuleInitializerGeneratorVisitor(module).visitModuleDefinition(module.context)
		HaxeUtils.createHaxeSourceFile(module, initializerName, outputDirectory, initializerContents)
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
