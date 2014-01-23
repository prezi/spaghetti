package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition

/**
 * Created by lptr on 12/11/13.
 */
class TypeScriptGenerator implements Generator {

	private final ModuleConfiguration config

	TypeScriptGenerator(ModuleConfiguration config) {
		this.config = config
	}

	@Override
	void generateModuleHeaders(ModuleDefinition module, File outputDirectory)
	{
		generateModuleInterface(module, outputDirectory)
		generateStuffForDependentModules(outputDirectory)
	}

	@Override
	void generateApplication(File outputDirectory)
	{
		generateStuffForDependentModules(outputDirectory)
	}

	@Override
	String processModuleJavaScript(ModuleDefinition module, String javaScript)
	{
		return \
"""${javaScript}
	var moduleImpl = new ${module.name.getFullyQualifiedName()}Impl();
	var constants = new ${module.name.getFullyQualifiedName()}Constants();
	moduleImpl.__consts = constants;
	return moduleImpl;
"""
	}

	@Override
	String processApplicationJavaScript(String javaScript)
	{
		return javaScript
	}

	private void generateStuffForDependentModules(File outputDirectory) {
		config.dependentModules.each { dependentModule ->
			generateStructuralTypesForModuleInterfaces(dependentModule, outputDirectory)
		}
	}

	/**
	 * Generates main interface for module.
	 */
	private void generateModuleInterface(ModuleDefinition module, File outputDirectory)
	{
		def contents = new TypeScriptModuleGeneratorVisitor(module, config.dependentModules, true).processModule()
		TypeScriptUtils.createSourceFile(module.name, outputDirectory, contents)
	}

	private void generateStructuralTypesForModuleInterfaces(ModuleDefinition module, File outputDirectory)
	{
		def moduleFileContents = new TypeScriptModuleGeneratorVisitor(module, config.dependentModules, false).processModule()
		TypeScriptUtils.createSourceFile(module.name, outputDirectory, moduleFileContents)
	}
}

