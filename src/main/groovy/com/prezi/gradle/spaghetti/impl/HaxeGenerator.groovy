package com.prezi.gradle.spaghetti.impl

import com.prezi.gradle.spaghetti.Generator
import com.prezi.gradle.spaghetti.MethodDefinition
import com.prezi.gradle.spaghetti.ModuleDefinition
import com.prezi.gradle.spaghetti.ServiceDefinition
import com.prezi.gradle.spaghetti.Type
import org.gradle.api.Named
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
	void generateInterfaces(ModuleDefinition moduleDef, File outputDirectory)
	{
		def namespace = moduleDef.namespace
		def packageDir = createModuleDirectory(namespace, outputDirectory)

		moduleDef.services.values().each { ServiceDefinition serviceDef ->
			def serviceFile = createHaxeSourceFile(namespace, packageDir, serviceDef)

			serviceFile << "interface ${serviceDef.name} {\n"
			serviceDef.methods.values().each { MethodDefinition methodDef ->
				serviceFile << "\tfunction ${methodDef.name}("
				serviceFile << methodDef.parameters.collect { String name, Type type ->
					return "${name} : ${typeName(type)}"
				}.join(", ")
				serviceFile << "):${typeName(methodDef.returnType)};\n"
			}
			serviceFile << "}\n"
		}
	}

	@Override
	void generateClientModule(ModuleDefinition moduleDef, File outputDirectory)
	{
		def namespace = moduleDef.namespace
		def packageDir = createModuleDirectory(namespace, outputDirectory)
		def moduleFile = createHaxeSourceFile(namespace, packageDir, moduleDef)
		moduleFile << "class ${moduleDef.name} {\n"
		moduleFile << "\n"
		moduleFile << "\tvar module:Dynamic;\n"
		moduleFile << "\n"
		moduleFile << "\tpublic function new() {\n"
		moduleFile << "\t\tthis.module = untyped require(\"${moduleDef.name}\");\n";
		moduleFile << "\t}\n"
		moduleFile << "\n"
		moduleDef.services.values().each { ServiceDefinition serviceDef ->
			moduleFile << "\tpublic function create${serviceDef.name}():${serviceDef.name} {\n"
			moduleFile << "\t\treturn new untyped module.${serviceDef.name}();\n"
			moduleFile << "\t}\n"
		}
		moduleFile << "\n"
		moduleFile << "}\n"
	}

	private File createModuleDirectory(String namespace, File outputDirectory)
	{
		def dir = outputDirectory
		if (namespace != null && !namespace.empty)
		{
			dir = new File(outputDirectory, namespace.replaceAll("\\.", "/"))
		}
		return project.mkdir(dir)
	}

	private static File createHaxeSourceFile(String namespace, File packageDir, Named definition)
	{
		def file = new File(packageDir, definition.name + ".hx")
		file.delete()
		if (namespace != null && !namespace.empty)
		{
			file << "package ${namespace};\n";
			file << "\n"
		}
		return file
	}

	private static String typeName(Type type) {
		return type.name
	}
}
