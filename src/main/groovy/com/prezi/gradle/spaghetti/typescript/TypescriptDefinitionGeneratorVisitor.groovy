package com.prezi.gradle.spaghetti.typescript

import com.prezi.gradle.spaghetti.ModuleConfiguration
import com.prezi.gradle.spaghetti.ModuleDefinition
import org.antlr.v4.runtime.misc.NotNull
import prezi.spaghetti.SpaghettiModuleParser
/**
 * Created by lptr on 16/11/13.
 */
abstract class TypescriptDefinitionGeneratorVisitor<T> extends TypescriptGeneratorVisitor<T> {
	protected def lastMethodParameter = null

	protected TypescriptDefinitionGeneratorVisitor(ModuleConfiguration config, ModuleDefinition module, File outputDirectory)
	{
		super(config, module, outputDirectory)
	}

	protected T generateTypeDefinition(SpaghettiModuleParser.TypeDefinitionContext ctx)
	{
		def previousFile = currentFile
		def typeName = ctx.name.text
		currentFile = createTypescriptSourceFile(typeName)

		addDocumentationIfNecessary(ctx.documentation)
		currentFile << "\texport interface ${typeName} {\n"
		def result = (T) super.visitTypeDefinition(ctx)
		currentFile << "\t}\n"
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
		def paramType = typescriptTypeName(ctx.type)
		if (lastMethodParameter != null)
		{
			currentFile << ", "
		}
		currentFile << "${paramName}: ${paramType}"
		lastMethodParameter = ctx
		return super.visitMethodParameterDefinition(ctx)
	}

	protected Object generateMethodDefinition(SpaghettiModuleParser.MethodDefinitionContext ctx)
	{
		addDocumentationIfNecessary(ctx.documentation)
		def methodName = ctx.name.text
		def returnType = typescriptTypeName(ctx.returnType)
		currentFile << "\t\t${methodName}("
		def result = super.visitMethodDefinition(ctx)
		currentFile << "): ${returnType};\n"
		lastMethodParameter = null
		return result
	}

}
