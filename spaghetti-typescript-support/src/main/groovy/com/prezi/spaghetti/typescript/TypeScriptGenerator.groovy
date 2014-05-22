package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.AbstractGenerator
import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.definition.ModuleDefinition

import static com.prezi.spaghetti.ReservedWords.MODULE
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
			copySpaghettiClass(outputDirectory)
			generateModuleInterface(module, outputDirectory)
		}
		config.directDependentModules.each { dependentModule ->
			generateStructuralTypesForModuleInterfaces(dependentModule, outputDirectory, true)
		}
		config.transitiveDependentModules.each { dependentModule ->
			generateStructuralTypesForModuleInterfaces(dependentModule, outputDirectory, false)
		}
	}

	@Override
	protected String processModuleJavaScriptInternal(ModuleDefinition module, ModuleConfiguration config, String javaScript)
	{
		// TODO This should be made type-safe
		def constructorParameters = [ CONFIG ] + config.directDynamicDependentModules.collect { ModuleDefinition dependency ->
			"${CONFIG}[\"${MODULES}\"][\"${dependency.name}\"][\"${MODULE}\"]"
		}
		return \
"""${javaScript}
var module = new ${module.name}.${module.alias}(${constructorParameters.join(", ")});
return {
	${MODULE}: module
};
"""
	}

	/**
	 * Copies Spaghetti.hx to the generated source directory.
	 */
	private static void copySpaghettiClass(File outputDirectory) {
		new File(outputDirectory, "${SPAGHETTI_MODULE_CONFIGURATION}.ts") << TypeScriptGenerator.class.getResourceAsStream("/${SPAGHETTI_MODULE_CONFIGURATION}.ts")
	}

	/**
	 * Generates main interface for module.
	 */
	private void generateModuleInterface(ModuleDefinition module, File outputDirectory)
	{
		def moduleClassName = "I${module.alias}"
		def contents = new TypeScriptModuleGeneratorVisitor(module, moduleClassName, config.allDependentModules, true).processModule()
		TypeScriptUtils.createSourceFile(module, moduleClassName, outputDirectory, contents)
	}

	private void generateStructuralTypesForModuleInterfaces(ModuleDefinition module, File outputDirectory, boolean generateModuleInterface)
	{
		def contents = new TypeScriptModuleGeneratorVisitor(module, module.alias, config.allDependentModules, generateModuleInterface).processModule()
		TypeScriptUtils.createSourceFile(module, module.alias, outputDirectory, contents)
	}
}
