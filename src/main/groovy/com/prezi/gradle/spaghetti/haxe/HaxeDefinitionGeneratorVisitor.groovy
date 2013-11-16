package com.prezi.gradle.spaghetti.haxe

import com.prezi.gradle.spaghetti.ModuleConfiguration
import com.prezi.gradle.spaghetti.ModuleDefinition
import org.antlr.v4.runtime.misc.NotNull
import prezi.spaghetti.SpaghettiModuleParser

/**
 * Created by lptr on 16/11/13.
 */
abstract class HaxeDefinitionGeneratorVisitor<T> extends HaxeGeneratorVisitor<T> {
	protected def lastMethodParameter = null

	protected HaxeDefinitionGeneratorVisitor(ModuleConfiguration config, ModuleDefinition module, File outputDirectory)
	{
		super(config, module, outputDirectory)
	}

	protected T generateTypeDefinition(SpaghettiModuleParser.TypeDefinitionContext ctx, Closure<String> defineType)
	{
		def previousFile = currentFile
		def typeName = ctx.name.text
		currentFile = createHaxeSourceFile(typeName)

		addDocumentationIfNecessary(ctx.documentation)
		currentFile << defineType(typeName)
		currentFile << " {\n"
		def result = (T) super.visitTypeDefinition(ctx)
		currentFile << "}\n"
		currentFile = previousFile
		return result
	}

	@Override
	T visitMethodDefinition(@NotNull @NotNull SpaghettiModuleParser.MethodDefinitionContext ctx)
	{
		T result = (T) super.visitMethodDefinition(ctx)
		lastMethodParameter = null
		return result
	}

	protected Object generateMethodParameter(SpaghettiModuleParser.MethodParameterDefinitionContext ctx)
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

	protected Object generateMethodDefinition(SpaghettiModuleParser.MethodDefinitionContext ctx)
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

}
