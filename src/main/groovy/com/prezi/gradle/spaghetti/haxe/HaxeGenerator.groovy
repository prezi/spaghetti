package com.prezi.gradle.spaghetti.haxe

import com.prezi.gradle.spaghetti.Generator
import com.prezi.gradle.spaghetti.ModuleConfiguration
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
			def visitor = new HaxeInterfaceGeneratorVisitor(config, module, outputDirectory)
			visitor.visit(module.context)
		}
	}

	@Override
	void generateClientModule(ModuleConfiguration config, File outputDirectory)
	{
		config.modules.values().each { module ->
			def visitor = new HaxeTypedefGeneratorVisitor(config, module, outputDirectory)
			visitor.visit(module.context)
		}
//		def namespace = moduleDef.namespace
//		def packageDir = createModuleDirectory(namespace, outputDirectory)
//		def moduleFile = createHaxeSourceFile(namespace, packageDir, moduleDef)
//		moduleFile << "class ${moduleDef.name} {\n"
//		moduleFile << "\n"
//		moduleFile << "\tvar module:Dynamic;\n"
//		moduleFile << "\n"
//		moduleFile << "\tpublic function new() {\n"
//		moduleFile << "\t\tthis.module = untyped require(\"${moduleDef.name}\");\n";
//		moduleFile << "\t}\n"
//		moduleFile << "\n"
//		moduleDef.types.values().each { TypeDefinition serviceDef ->
//			moduleFile << "\tpublic function create${serviceDef.name}():${serviceDef.name} {\n"
//			moduleFile << "\t\treturn new untyped module.${serviceDef.name}();\n"
//			moduleFile << "\t}\n"
//		}
//		moduleFile << "\n"
//		moduleFile << "}\n"
	}
}
