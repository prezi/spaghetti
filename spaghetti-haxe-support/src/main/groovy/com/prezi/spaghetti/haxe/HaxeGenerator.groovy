package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractGenerator
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.config.ModuleConfiguration
import com.prezi.spaghetti.haxe.access.HaxeModuleAccessorGeneratorVisitor
import com.prezi.spaghetti.haxe.impl.HaxeModuleInitializerGeneratorVisitor
import com.prezi.spaghetti.haxe.impl.HaxeModuleInterfaceGeneratorVisitor
import com.prezi.spaghetti.haxe.impl.HaxeModuleStaticProxyGeneratorVisitor

import static com.prezi.spaghetti.ReservedWords.SPAGHETTI_MODULE_CONFIGURATION

class HaxeGenerator extends AbstractGenerator {

	// Workaround variable to trick Haxe into exposing the module
	public static final String HAXE_MODULE_VAR = "__haxeModule"

	HaxeGenerator(ModuleConfiguration config) {
		super(config)
	}

	@Override
	void generateHeaders(File outputDirectory) {
		config.localModules.each { module ->
			copySpaghettiClass(outputDirectory)
			generateModuleInterface(module, outputDirectory)
			generateModuleInitializer(module, config.directDependentModules, outputDirectory)
			generateModuleStaticProxy(module, outputDirectory)
			generateModuleTypes(module, outputDirectory)
		}
		config.allDependentModules.each { dependentModule ->
			generateModuleTypes(dependentModule, outputDirectory)
		}
		config.directDependentModules.each { dependentModule ->
			generateModuleAccessor(dependentModule, outputDirectory)
		}
	}

	@Override
	protected String processModuleJavaScriptInternal(ModuleNode module, String javaScript)
	{
"""// Haxe expects either window or exports to be present
var exports = exports || {};
var ${HAXE_MODULE_VAR};
${javaScript}
return ${HAXE_MODULE_VAR};
"""
	}

	/**
	 * Copies SpaghettiConfiguration.hx to the generated source directory.
	 */
	private static void copySpaghettiClass(File outputDirectory) {
		new File(outputDirectory, "${SPAGHETTI_MODULE_CONFIGURATION}.hx") << HaxeGenerator.class.getResourceAsStream("/${SPAGHETTI_MODULE_CONFIGURATION}.hx")
	}

	/**
	 * Generates main interface for module.
	 */
	private static void generateModuleInterface(ModuleNode module, File outputDirectory)
	{
		def contents = new HaxeModuleInterfaceGeneratorVisitor().visit(module)
		HaxeUtils.createHaxeSourceFile(module.name, "I${module.alias}", outputDirectory, contents)
	}

	/**
	 * Generates static proxy.
	 */
	private static void generateModuleStaticProxy(ModuleNode module, File outputDirectory)
	{
		def contents = new HaxeModuleStaticProxyGeneratorVisitor(module).visit(module)
		HaxeUtils.createHaxeSourceFile(module.name, "__${module.alias}Static", outputDirectory, contents)
	}

	/**
	 * Generates initializer for module.
	 */
	private static void generateModuleInitializer(ModuleNode module, Collection<ModuleNode> dependencies, File outputDirectory)
	{
		def initializerName = "__" + module.alias + "Init"
		def initializerContents = new HaxeModuleInitializerGeneratorVisitor(dependencies).visit(module)
		HaxeUtils.createHaxeSourceFile(module.name, initializerName, outputDirectory, initializerContents)
	}

	/**
	 * Generates accessor class for module.
	 */
	private static void generateModuleAccessor(ModuleNode module, File outputDirectory)
	{
		def contents = new HaxeModuleAccessorGeneratorVisitor().visit(module)
		HaxeUtils.createHaxeSourceFile(module.name, module.alias, outputDirectory, contents)
	}

	/**
	 * Generates interfaces, enums, structs and constants defined in the module.
	 */
	private static void generateModuleTypes(ModuleNode module, File outputDirectory)
	{
		new HaxeDefinitionIteratorVisitor(outputDirectory, module.name).visit(module)
	}
}
