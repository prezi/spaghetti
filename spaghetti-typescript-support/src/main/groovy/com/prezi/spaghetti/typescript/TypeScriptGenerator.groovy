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

	public static final String CREATE_MODULE_FUNCTION = "__createSpaghettiModule"

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
		return \
"""${javaScript}
return ${module.name}.${CREATE_MODULE_FUNCTION}(${CONFIG});
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
		def contents = new TypeScriptModuleGeneratorVisitor(config, module, moduleClassName, true).processModule()

		def directDynamicDependentModules = config.directDynamicDependentModules
		def dynamicInstances = []

		directDynamicDependentModules.eachWithIndex { ModuleDefinition dependency, int index ->
			dynamicInstances.add "var dependency${index}:${dependency.name}.${dependency.alias} = ${CONFIG}[\"${MODULES}\"][\"${dependency.name}\"][\"${MODULE}\"];"
		}
		def dynamicReferences = ["${CONFIG}"] + (0..<dynamicInstances.size()).collect { "dependency${it}" }

		contents += """export function ${CREATE_MODULE_FUNCTION}(config:any):any {
	${dynamicInstances.join("\n\t")}
	var module:${moduleClassName} = new ${module.alias}(${dynamicReferences.join(", ")});
	return {
		${MODULE}: module
	}
}
"""

		TypeScriptUtils.createSourceFile(module, moduleClassName, outputDirectory, contents)
	}

	private void generateStructuralTypesForModuleInterfaces(ModuleDefinition module, File outputDirectory, boolean generateModuleInterface)
	{
		def contents = new TypeScriptModuleGeneratorVisitor(config, module, module.alias, generateModuleInterface).processModule()
		TypeScriptUtils.createSourceFile(module, module.alias, outputDirectory, contents)
	}
}
