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
		// Generate module interface
		HaxeUtils.createHaxeSourceFile(module.name, outputDirectory,
			new HaxeModuleInterfaceGeneratorVisitor(config, module).processModule()
		)

		// Generate module initializer
		def initializerName = "__" + module.name.localName + "Init"
		def initializerContents = """class ${initializerName} {
	public static function __init__() {
		var module:${module.name.localName} = new ${module.name.localName}Impl();
		untyped __module = module;
	}
}
"""
		HaxeUtils.createHaxeSourceFile(initializerName, module.name, outputDirectory, initializerContents)

		// Generate interfaces the module should implement
		new HaxeTypeGeneratorVisitor(module, outputDirectory, {
			new HaxeTypeInterfaceGeneratorVisitor(config, module)
		}).processModule()

		def modulesName = module.name.resolveLocalName(FQName.fromString("Modules"))
		generateDependentModules(config, modulesName, outputDirectory)

	}

	@Override
	void generateApplication(ModuleConfiguration config, File outputDirectory)
	{
		def modulesName = FQName.fromString("prezi.test.client.Modules")
		generateDependentModules(config, modulesName, outputDirectory)
	}

	private static void generateDependentModules(ModuleConfiguration config, FQName modulesName, File outputDirectory) {
		// Find all dependent modules
		def dependentModules = config.dependentModules

		// Generate typedefs for each dependent module
		dependentModules.each { dependentModule ->
			HaxeUtils.createHaxeSourceFile(dependentModule.name, outputDirectory,
				new HaxeModuleTypedefGeneratorVisitor(config, dependentModule).processModule()
			)
			new HaxeTypeGeneratorVisitor(dependentModule, outputDirectory, {
				new HaxeTypeTypedefGeneratorVisitor(config, dependentModule)
			}).visit(dependentModule.context)
		}

		// Generate Modules.hx to access dependent modules
		if (!dependentModules.empty) {
			String modulesContents = """class ${modulesName.localName} {

	static var modules:Array<Dynamic>;

	static function __init__() {
		modules = untyped __modules;
	}
"""
			dependentModules.eachWithIndex { module, index ->
				modulesContents += """
	public static inline function get${module.name.localName}():${module.name} {
		return modules[${index}];
	}
"""
			}
			modulesContents += "}\n"
			HaxeUtils.createHaxeSourceFile(modulesName, outputDirectory, modulesContents)
		}
	}

	@Override
	String processModuleJavaScript(ModuleConfiguration config, ModuleDefinition module, String javaScript)
	{
		return "var __module;\n" + javaScript + "return __module;\n"
	}
}
