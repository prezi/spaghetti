package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.FQName
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

		def modulesClassName = module.name.qualifyLocalName(FQName.fromString("Modules"))
		generateStuffForDependentModules(modulesClassName, outputDirectory)
	}

	@Override
	void generateApplication(String namespace, File outputDirectory)
	{
		def modulesClassName = FQName.fromString("${namespace}.Modules")
		generateStuffForDependentModules(modulesClassName, outputDirectory)
	}

	@Override
	String processModuleJavaScript(ModuleDefinition module, String javaScript)
	{
		return \
"""
${javaScript}
return new ${module.name.getFullyQualifiedName()}Impl();
"""
	}

	@Override
	String processApplicationJavaScript(String javaScript)
	{
		return javaScript
	}

	private void generateStuffForDependentModules(FQName modulesClassName, File outputDirectory) {
		config.dependentModules.each { dependentModule ->
			generateStructuralTypesForModuleInterfaces(dependentModule, outputDirectory)
		}
		generateClassToAccessDependentModules(modulesClassName, outputDirectory)
	}

	/**
	 * Generates main interface for module.
	 */
	private static void generateModuleInterface(ModuleDefinition module, File outputDirectory)
	{
		def contents = new TypeScriptModuleGeneratorVisitor(module).processModule()
		TypeScriptUtils.createSourceFile(module.name, outputDirectory, contents)
	}

	/**
	 * Generates Modules.hx with methods like <code>get<ModuleName>():<ModuleName> { ... }</code>.
	 */
	private void generateClassToAccessDependentModules(FQName modulesClassName, File outputDirectory)
	{
		def dependentModules = config.dependentModules

		// Generate Modules.hx to access dependent modules
		if (!dependentModules.empty)
		{
			String modulesContents =
"""
declare var __modules:Array<any>;
class ${modulesClassName.localName} {
"""
			dependentModules.eachWithIndex { module, index ->
				modulesContents +=
"""\tget${module.name.localName}():${module.name} {
		return __modules[${index}];
	}
"""
			}
			modulesContents +=
"""}
"""
			TypeScriptUtils.createSourceFile(modulesClassName, outputDirectory, modulesContents)
		}
	}

	private static void generateStructuralTypesForModuleInterfaces(ModuleDefinition module, File outputDirectory)
	{
		def moduleFileContents = new TypeScriptModuleGeneratorVisitor(module).processModule()
		TypeScriptUtils.createDeclarationSourceFile(module.name, outputDirectory, moduleFileContents)
	}
}

