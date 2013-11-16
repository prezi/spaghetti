package com.prezi.gradle.spaghetti.impl

import com.prezi.gradle.spaghetti.FQName
import com.prezi.gradle.spaghetti.Generator
import com.prezi.gradle.spaghetti.ModuleConfiguration
import com.prezi.gradle.spaghetti.ModuleDefinition
import org.antlr.v4.runtime.misc.NotNull
import org.gradle.api.Named
import org.gradle.api.Project
import prezi.spaghetti.SpaghettiModuleBaseVisitor
import prezi.spaghetti.SpaghettiModuleParser
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
			def visitor = new InterfaceGenerator(config, module, outputDirectory)
			visitor.visit(module.context)
		}
	}

	@Override
	void generateClientModule(ModuleConfiguration config, File outputDirectory)
	{
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
}

class InterfaceGenerator extends SpaghettiModuleBaseVisitor<Object> {

	private final ModuleConfiguration config
	private final ModuleDefinition module
	private final File outputDirectory

	private File moduleFile = null
	private File currentTypeFile = null
	private def lastMethodParameter = null

	InterfaceGenerator(ModuleConfiguration config, ModuleDefinition module, File outputDirectory) {
		this.config = config
		this.module = module
		this.outputDirectory = module.name.createNamespacePath(outputDirectory)
	}

	@Override
	Object visitModuleDefinition(@NotNull @NotNull SpaghettiModuleParser.ModuleDefinitionContext ctx)
	{
		def moduleName = module.name.localName
		this.outputDirectory.mkdirs()
		this.moduleFile = new File(outputDirectory, moduleName + ".hx")
		moduleFile.delete()

		moduleFile << "class ${moduleName} {\n"
		currentTypeFile = moduleFile
		def result = super.visitModuleDefinition(ctx);
		moduleFile << "}\n"

		return result
	}

	@Override
	Object visitTypeDefinition(@NotNull @NotNull SpaghettiModuleParser.TypeDefinitionContext ctx)
	{
		def typeName = ctx.name.text
		currentTypeFile = new File(outputDirectory, typeName + ".hx")
		currentTypeFile.delete()
		currentTypeFile << "interface ${typeName} {\n"
		def result = super.visitTypeDefinition(ctx)
		currentTypeFile << "}\n"
		currentTypeFile = moduleFile
		return result
	}

	@Override
	Object visitMethodDefinition(@NotNull @NotNull SpaghettiModuleParser.MethodDefinitionContext ctx)
	{
		def methodName = ctx.name.text
		def returnType = config.resolveTypeName(ctx.returnType.text, module.name)
//		if (!config.types.containsKey(returnType)) {
//			throw new IllegalStateException("Return type not found: " + returnType)
//		}
		currentTypeFile << "\tfunction ${methodName}("
		def result = super.visitMethodDefinition(ctx)
		currentTypeFile << "):${returnType};\n"
		lastMethodParameter = null
		return result
	}

	@Override
	Object visitMethodParameterDefinition(@NotNull @NotNull SpaghettiModuleParser.MethodParameterDefinitionContext ctx)
	{
		def paramName = ctx.name.text
		def paramType = config.resolveTypeName(ctx.type.text, module.name)
//		if (!config.types.containsKey(paramType)) {
//			throw new IllegalStateException("Type not found: " + paramType)
//		}
		if (lastMethodParameter != null) {
			currentTypeFile << ", "
		}
		currentTypeFile << "${paramName} : ${paramType}"
		lastMethodParameter = ctx
		return super.visitMethodParameterDefinition(ctx)
	}

	static def HAXE_TYPE_NAME_CONVERSION = [
			(ModuleConfiguration.TYPE_VOID): "Void",
			(ModuleConfiguration.TYPE_BOOL): "Bool",
			(ModuleConfiguration.TYPE_INT): "Int",
			(ModuleConfiguration.TYPE_FLOAT): "Float",
			(ModuleConfiguration.TYPE_STRING): "String"
	]

	static String haxeTypeName(FQName typeName) {
		return HAXE_TYPE_NAME_CONVERSION.get(typeName) ?: typeName.fullyQualifiedName
	}
}
