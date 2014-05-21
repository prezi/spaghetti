package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractGenerator
import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.definition.ModuleDefinition

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
			generateModuleInterface(module, "I${module.alias}", outputDirectory)
			generateModuleInitializer(module, config.directDynamicDependentModules, outputDirectory)
			generateInterfacesForModuleTypes(module, outputDirectory, false)
		}
		config.allDependentModules.each { dependentModule ->
			generateInterfacesForModuleTypes(dependentModule, outputDirectory, true)
		}
		config.allDynamicDependentModules.each { dependentModule ->
			generateModuleInterface(dependentModule, dependentModule.alias, outputDirectory)
		}
		config.allStaticDependentModules.each { dependentModule ->
			generateModuleProxy(dependentModule, outputDirectory)
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
	private static void generateModuleInterface(ModuleDefinition module, String className, File outputDirectory)
	{
		def contents = new HaxeModuleInterfaceGeneratorVisitor(module, className).processModule()
		HaxeUtils.createHaxeSourceFile(module, className, outputDirectory, contents)
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
	private static void generateModuleInitializer(ModuleDefinition module, Collection<ModuleDefinition> dynamicDependencies, File outputDirectory)
	{
		def initializerName = getInitializerName(module)
		def initializerContents = new HaxeModuleInitializerGeneratorVisitor(module, dynamicDependencies).visitModuleDefinition(module.context)
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
