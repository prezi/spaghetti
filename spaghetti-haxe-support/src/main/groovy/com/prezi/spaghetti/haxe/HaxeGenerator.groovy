package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractGenerator
import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.haxe.access.HaxeModuleAccessorGeneratorVisitor
import com.prezi.spaghetti.haxe.impl.HaxeModuleInitializerGeneratorVisitor
import com.prezi.spaghetti.haxe.impl.HaxeModuleInterfaceGeneratorVisitor
import com.prezi.spaghetti.haxe.impl.HaxeModuleStaticProxyGeneratorVisitor

import static com.prezi.spaghetti.ReservedWords.SPAGHETTI_MODULE_CONFIGURATION

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
			copySpaghettiClass(outputDirectory)
			generateModuleInterface(module, outputDirectory)
			generateModuleInitializer(module, config.directDependentModules, outputDirectory)
			generateModuleStaticProxy(module, outputDirectory)
			generateModuleTypes(module, outputDirectory, false)
		}
		config.allDependentModules.each { dependentModule ->
			generateModuleTypes(dependentModule, outputDirectory, true)
		}
		config.directDependentModules.each { dependentModule ->
			generateModuleAccessor(dependentModule, outputDirectory)
		}
	}

	@Override
	protected String processModuleJavaScriptInternal(ModuleDefinition module, ModuleConfiguration config, String javaScript)
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
	 * Copies SpaghettiModuleConfiguration.hx to the generated source directory.
	 */
	private static void copySpaghettiClass(File outputDirectory) {
		new File(outputDirectory, "${SPAGHETTI_MODULE_CONFIGURATION}.hx") << HaxeGenerator.class.getResourceAsStream("/${SPAGHETTI_MODULE_CONFIGURATION}.hx")
	}

	/**
	 * Generates main interface for module.
	 */
	private static void generateModuleInterface(ModuleDefinition module, File outputDirectory)
	{
		def contents = new HaxeModuleInterfaceGeneratorVisitor(module).processModule()
		HaxeUtils.createHaxeSourceFile(module, "I${module.alias}", outputDirectory, contents)
	}

	/**
	 * Generates static proxy.
	 */
	private static void generateModuleStaticProxy(ModuleDefinition module, File outputDirectory)
	{
		def contents = new HaxeModuleStaticProxyGeneratorVisitor(module).processModule()
		HaxeUtils.createHaxeSourceFile(module, "__${module.alias}Static", outputDirectory, contents)
	}

	/**
	 * Generates initializer for module.
	 */
	private static void generateModuleInitializer(ModuleDefinition module, Collection<ModuleDefinition> dependencies, File outputDirectory)
	{
		def initializerName = "__" + module.alias + "Init"
		def initializerContents = new HaxeModuleInitializerGeneratorVisitor(module, dependencies).visitModuleDefinition(module.context)
		HaxeUtils.createHaxeSourceFile(module, initializerName, outputDirectory, initializerContents)
	}

	/**
	 * Generates accessor class for module.
	 */
	private static void generateModuleAccessor(ModuleDefinition module, File outputDirectory)
	{
		def contents = new HaxeModuleAccessorGeneratorVisitor(module).processModule()
		HaxeUtils.createHaxeSourceFile(module, module.alias, outputDirectory, contents)
	}

	/**
	 * Generates interfaces, enums, structs and constants defined in the module.
	 */
	private static void generateModuleTypes(ModuleDefinition module, File outputDirectory, boolean dependentModule)
	{
		new HaxeDefinitionIteratorVisitor(module, outputDirectory, dependentModule, {
			new HaxeInterfaceGeneratorVisitor(module)
		}).processModule()
	}
}
