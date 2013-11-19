package com.prezi.gradle.spaghetti.typescript

import com.prezi.gradle.spaghetti.ModuleConfiguration
import com.prezi.gradle.spaghetti.ModuleDefinition
import org.antlr.v4.runtime.misc.NotNull
import prezi.spaghetti.SpaghettiModuleParser
/**
 * Created by lptr on 16/11/13.
 */
class TypescriptInterfaceGeneratorVisitor extends TypescriptDefinitionGeneratorVisitor<Object> {

	TypescriptInterfaceGeneratorVisitor(ModuleConfiguration config, ModuleDefinition module, File outputDirectory)
	{
		super(config, module, outputDirectory)
	}

	@Override
	Object visitModuleDefinition(@NotNull @NotNull SpaghettiModuleParser.ModuleDefinitionContext ctx)
	{
		def moduleName = module.name.localName
		File moduleFile = createTypescriptSourceFile(moduleName)
		currentFile = moduleFile

		addDocumentationIfNecessary(ctx.documentation)

		moduleFile << "\texport interface ${moduleName} {\n"
		def result = super.visitModuleDefinition(ctx);
		moduleFile << "\t}\n"
		moduleFile << "\n"
		moduleFile << "\tdeclare var __module: ${moduleName} = new ${moduleName}Impl();"
		moduleFile << "}\n"

		// Make sure nothing nasty happens later
		currentFile = null

		return result
	}

	@Override
	Object visitTypeDefinition(@NotNull @NotNull SpaghettiModuleParser.TypeDefinitionContext ctx)
	{
		return generateTypeDefinition(ctx)
	}

	@Override
	Object visitMethodDefinition(@NotNull @NotNull SpaghettiModuleParser.MethodDefinitionContext ctx)
	{
		return generateMethodDefinition(ctx)
	}

	@Override
	Object visitMethodParameterDefinition(@NotNull @NotNull SpaghettiModuleParser.MethodParameterDefinitionContext ctx)
	{
		return generateMethodParameter(ctx)
	}
}
