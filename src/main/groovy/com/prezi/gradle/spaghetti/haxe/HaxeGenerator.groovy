package com.prezi.gradle.spaghetti.haxe

import com.prezi.gradle.spaghetti.FQName
import com.prezi.gradle.spaghetti.Generator
import com.prezi.gradle.spaghetti.ModuleConfiguration
import com.prezi.gradle.spaghetti.ModuleDefinition
import org.gradle.api.Project
/**
 * Created by lptr on 12/11/13.
 */
class HaxeGenerator implements Generator {
	private Project project

	@Override
	void initialize(Project project)
	{
		this.project = project
	}

	@Override
	String getPlatform()
	{
		return "haxe"
	}

	@Override
	void generateInterfaces(ModuleConfiguration config, ModuleDefinition module, File outputDirectory)
	{
		// Generate interfaces the module should implement
		new HaxeInterfaceGeneratorVisitor(config, module, outputDirectory).visit(module.context)

		// Find all dependent modules
		def dependentModules = config.modules.values().findAll { dependentModule -> dependentModule != module }

		// Generate typedefs for each dependent module
		dependentModules.each { dependentModule ->
			new HaxeTypedefGeneratorVisitor(config, dependentModule, outputDirectory).visit(module.context)
		}

		// Generate Modules.hx to access dependent modules
		if (!dependentModules.empty) {
			generateModulesFile(module.name.resolveLocalName("Modules"), outputDirectory, dependentModules)
		}
	}

	@Override
	void generateClientModule(ModuleConfiguration config, File outputDirectory)
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
		return javaScript + "return new ${module.name.fullyQualifiedName}Impl();\n"
	}

	private static void generateModulesFile(FQName modulesName, File outputDirectory, Iterable<ModuleDefinition> dependencies)
	{
		def modulesFile = HaxeGeneratorVisitor.createHaxeSourceFile(modulesName, outputDirectory)
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
