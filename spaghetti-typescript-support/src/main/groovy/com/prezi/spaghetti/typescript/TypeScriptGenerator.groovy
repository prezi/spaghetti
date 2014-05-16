package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.AbstractGenerator
import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.definition.ModuleDefinition
import groovy.text.SimpleTemplateEngine

import static com.prezi.spaghetti.ReservedWords.MODULE
import static com.prezi.spaghetti.ReservedWords.CONSTANTS

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
	String processModuleJavaScript(ModuleDefinition module, String javaScript)
	{

		return \
"""${javaScript}
	var module = new ${module.name}.${module.alias}Impl();
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
		def template = new SimpleTemplateEngine().createTemplate(TypeScriptGenerator.class.getResource("/Spaghetti.ts"))
		new File(outputDirectory, "Spaghetti.ts") << template.make(moduleName: module.name)
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
