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
	void generateInterfaces(ModuleConfiguration config, File outputDirectory)
	{
		config.modules.values().each { module ->
			new HaxeInterfaceGeneratorVisitor(config, module, outputDirectory).visit(module.context)
		}
	}

	@Override
	void generateClientModule(ModuleConfiguration config, File outputDirectory)
	{
		config.modules.values().each { module ->
			new HaxeTypedefGeneratorVisitor(config, module, outputDirectory).visit(module.context)
		}
		def modulesName = FQName.fromString("prezi.client.Modules")
		generateModulesFile(modulesName, outputDirectory, config.modules.values())
	}

	private static void generateModulesFile(FQName moduleName, File outputDirectory, Iterable<ModuleDefinition> dependencies)
	{
		def modulesFile = HaxeGeneratorVisitor.createHaxeSourceFile(moduleName, outputDirectory)
		modulesFile << """class Modules {

	var modules:Array<Dynamic>;

	function new(modules:Array<Dynamic>) {
		this.modules = modules;
	}
"""
		dependencies.eachWithIndex { module, index ->
			modulesFile << "\n"
			modulesFile << "\tpublic inline function get${module.name.localName}():${module.name} {\n"
			modulesFile << "\t\treturn modules[${index}];\n"
			modulesFile << "\t}\n"
		}
		modulesFile << "}\n"
	}
}
