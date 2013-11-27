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
		generateModuleInitializer(module, outputDirectory)

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
"""var __module;
${javaScript}
return __module;
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
	 * Generates initializer for module.
	 */
	private static void generateModuleInitializer(ModuleDefinition module, File outputDirectory)
	{
		def initializerName = "__" + module.name.localName + "Init"
		def initializerContents =
"""declare var __module:${module.name.localName};
__module = new ${module.name.localName}Impl();
"""
		TypeScriptUtils.createSourceFile(initializerName, module.name, outputDirectory, initializerContents)
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
"""class ${modulesClassName.localName} {

	static var modules:Array<Dynamic>;

	static function __init__() {
		modules = untyped __modules;
	}
"""
			dependentModules.eachWithIndex { module, index ->
				modulesContents +=
"""
	public static inline function get${module.name.localName}():${module.name} {
		return modules[${index}];
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
		TypeScriptUtils.createSourceFile(module.name, outputDirectory, moduleFileContents)
	}
}

