package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.AbstractGenerator
import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.definition.ModuleDefinition
import groovy.text.SimpleTemplateEngine

import static com.prezi.spaghetti.ReservedWords.MODULE
import static com.prezi.spaghetti.ReservedWords.CONSTANTS
import static com.prezi.spaghetti.ReservedWords.MODULES
import static com.prezi.spaghetti.ReservedWords.SPAGHETTI_MODULE_CONFIGURATION

/**
 * Created by lptr on 12/11/13.
 */
class TypeScriptGenerator extends AbstractGenerator {

	private final ModuleConfiguration config

	TypeScriptGenerator(ModuleConfiguration config) {
		this.config = config
	}

	@Override
	void generateHeaders(File outputDirectory) {
		config.localModules.each { module ->
			copySpaghettiClass(module, outputDirectory)
			generateModuleInterface(module, outputDirectory)
		}
		config.dependentModules.each { dependentModule ->
			generateStructuralTypesForModuleInterfaces(dependentModule, outputDirectory)
		}
	}

	@Override
	protected String processModuleJavaScriptInternal(ModuleDefinition module, ModuleConfiguration config, String javaScript)
	{
		// TODO This should be made type-safe
		def constructorParameters = [ CONFIG ] + config.dynamicDependentModules.collect { ModuleDefinition dependency ->
			"${CONFIG}[\"${MODULES}\"][\"${dependency.name}\"][\"${MODULE}\"]"
		}
		return \
"""${javaScript}
var module = new ${module.name}.${module.alias}Impl(${constructorParameters.join(", ")});
var consts = new ${module.name}.__${module.alias}Constants();
return {
	${MODULE}: module,
	${CONSTANTS}: consts
};
"""
	}

	/**
	 * Copies Spaghetti.hx to the generated source directory.
	 */
	private static void copySpaghettiClass(ModuleDefinition module, File outputDirectory) {
		new File(outputDirectory, "${SPAGHETTI_MODULE_CONFIGURATION}.ts") << TypeScriptGenerator.class.getResourceAsStream("/${SPAGHETTI_MODULE_CONFIGURATION}.ts")
	}

	/**
	 * Generates main interface for module.
	 */
	private void generateModuleInterface(ModuleDefinition module, File outputDirectory)
	{
		def contents = new TypeScriptModuleGeneratorVisitor(module, config.dependentModules, true).processModule()
		TypeScriptUtils.createSourceFile(module, module.alias, outputDirectory, contents)
	}

	private void generateStructuralTypesForModuleInterfaces(ModuleDefinition module, File outputDirectory)
	{
		def moduleFileContents = new TypeScriptModuleGeneratorVisitor(module, config.dependentModules, false).processModule()
		TypeScriptUtils.createSourceFile(module, module.alias, outputDirectory, moduleFileContents)
	}
}
