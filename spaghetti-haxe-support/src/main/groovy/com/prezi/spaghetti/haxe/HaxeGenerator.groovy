package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
/**
 * Created by lptr on 12/11/13.
 */
class HaxeGenerator extends Generator {

	HaxeGenerator() {
		super("haxe")
	}

	@Override
	void generateModuleHeaders(ModuleConfiguration config, ModuleDefinition module, File outputDirectory)
	{
		// Generate interfaces the module should implement
		new HaxeInterfaceGeneratorVisitor(config, module, outputDirectory).processModule()

		// Find all dependent modules
		def dependentModules = config.dependentModules

		// Generate typedefs for each dependent module
		config.dependentModules.each { dependentModule ->
			new HaxeTypedefGeneratorVisitor(config, dependentModule, outputDirectory).processModule()
		}

		// Generate Modules.hx to access dependent modules
		if (!dependentModules.empty) {
			generateModulesFile(module.name.resolveLocalName(FQName.fromString("Modules")), outputDirectory, dependentModules)
		}
	}

	@Override
	void generateApplication(ModuleConfiguration config, File outputDirectory)
	{
		// Generate typedefs for all modules
		config.modules.values().each { module ->
			new HaxeTypedefGeneratorVisitor(config, module, outputDirectory).visit(module.context)
		}

		// Generate Modules.hx to access dependent modules
		def modulesName = FQName.fromString("prezi.test.client.Modules")
		generateModulesFile(modulesName, outputDirectory, config.modules.values())
	}

	@Override
	String processModuleJavaScript(ModuleConfiguration config, ModuleDefinition module, String javaScript)
	{
		return "var __module;\n" + javaScript + "return __module;\n"
	}

	private static void generateModulesFile(FQName modulesName, File outputDirectory, Iterable<ModuleDefinition> dependencies)
	{
		def modulesFile = HaxeUtils.createHaxeSourceFile(modulesName, outputDirectory)
		modulesFile << """class ${modulesName.localName} {

	static var modules:Array<Dynamic>;

	static function __init__() {
		modules = untyped __modules;
	}
"""
		dependencies.eachWithIndex { module, index ->
			modulesFile << "\n"
			modulesFile << "\tpublic static inline function get${module.name.localName}():${module.name} {\n"
			modulesFile << "\t\treturn modules[${index}];\n"
			modulesFile << "\t}\n"
		}
		modulesFile << "}\n"
	}
}
