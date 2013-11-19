package com.prezi.gradle.spaghetti.typescript

import com.prezi.gradle.spaghetti.FQName
import com.prezi.gradle.spaghetti.Generator
import com.prezi.gradle.spaghetti.ModuleConfiguration
import com.prezi.gradle.spaghetti.ModuleDefinition

/**
 * Created by lptr on 18/11/13.
 */
class TypescriptGenerator extends Generator {
	TypescriptGenerator() {
		super("typescript")
	}

	@Override
	void generateInterfaces(ModuleConfiguration config, ModuleDefinition module, File outputDirectory)
	{
		// Generate interfaces the module should implement
		new TypescriptInterfaceGeneratorVisitor(config, module, outputDirectory).visit(module.context)

		// Find all dependent modules
		def dependentModules = config.dependentModules

		// Generate typedefs for each dependent module
		dependentModules.each { dependentModule ->
			new TypescriptTypedefGeneratorVisitor(config, dependentModule, outputDirectory).visit(dependentModule.context)
		}

		// Generate Modules.hx to access dependent modules
		if (!dependentModules.empty) {
			generateModulesFile(module.name.resolveLocalName(FQName.fromString("Modules")), outputDirectory, dependentModules)
		}
	}

	@Override
	void generateClientModule(ModuleConfiguration config, File outputDirectory)
	{
		// Generate typedefs for all modules
		config.modules.values().each { module ->
			new TypescriptTypedefGeneratorVisitor(config, module, outputDirectory).visit(module.context)
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
		def modulesFile = TypescriptGeneratorVisitor.createTypescriptSourceFile(modulesName, outputDirectory)
		modulesFile << """\tdeclare var __modules:any[];

	export class ${modulesName.localName} {

		static modules:any[] = __modules;
"""
		dependencies.eachWithIndex { module, index ->
			modulesFile << "\n"
			modulesFile << "\t\tstatic get${module.name.localName}():${module.name} {\n"
			modulesFile << "\t\t\treturn modules[${index}];\n"
			modulesFile << "\t\t}\n"
		}
		modulesFile << "\t}\n"
		modulesFile << "}\n"
	}
}
