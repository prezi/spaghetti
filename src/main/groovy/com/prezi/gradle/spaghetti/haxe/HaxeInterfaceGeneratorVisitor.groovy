package com.prezi.gradle.spaghetti.haxe

import com.prezi.gradle.spaghetti.ModuleConfiguration
import com.prezi.gradle.spaghetti.ModuleDefinition
import org.antlr.v4.runtime.misc.NotNull
import prezi.spaghetti.SpaghettiModuleParser

/**
 * Created by lptr on 16/11/13.
 */
class HaxeInterfaceGeneratorVisitor extends HaxeGeneratorVisitor<Object> {

	private File moduleFile
	private def lastMethodParameter = null

	HaxeInterfaceGeneratorVisitor(ModuleConfiguration config, ModuleDefinition module, File outputDirectory)
	{
		super(config, module, outputDirectory)
	}

	@Override
	Object visitModuleDefinition(@NotNull @NotNull SpaghettiModuleParser.ModuleDefinitionContext ctx)
	{
		def moduleName = module.name.localName
		moduleFile = createHaxeSourceFile(moduleName)
		currentFile = moduleFile

		addDocumentationIfNecessary(ctx.documentation)

		moduleFile << "class ${moduleName} {\n"
		def result = super.visitModuleDefinition(ctx);
		moduleFile << "}\n"

		// Make sure nothing nasty happens later
		moduleFile = null
		currentFile = null

		return result
	}

	@Override
	Object visitTypeDefinition(@NotNull @NotNull SpaghettiModuleParser.TypeDefinitionContext ctx)
	{
		def typeName = ctx.name.text
		currentFile = createHaxeSourceFile(typeName)

		addDocumentationIfNecessary(ctx.documentation)
		currentFile << "interface ${typeName} {\n"
		def result = super.visitTypeDefinition(ctx)
		currentFile << "}\n"
		currentFile = moduleFile
		return result
	}

	@Override
	Object visitMethodDefinition(@NotNull @NotNull SpaghettiModuleParser.MethodDefinitionContext ctx)
	{
		addDocumentationIfNecessary(ctx.documentation)
		def methodName = ctx.name.text
		def returnType = haxeTypeName(ctx.returnType.text)
		currentFile << "\tfunction ${methodName}("
		def result = super.visitMethodDefinition(ctx)
		currentFile << "):${returnType};\n"
		lastMethodParameter = null
		return result
	}

	@Override
	Object visitMethodParameterDefinition(@NotNull @NotNull SpaghettiModuleParser.MethodParameterDefinitionContext ctx)
	{
		def paramName = ctx.name.text
		def paramType = haxeTypeName(ctx.type.text)
		if (lastMethodParameter != null)
		{
			currentFile << ", "
		}
		currentFile << "${paramName}:${paramType}"
		lastMethodParameter = ctx
		return super.visitMethodParameterDefinition(ctx)
	}
}
