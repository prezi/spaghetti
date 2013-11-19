package com.prezi.gradle.spaghetti.haxe

import com.prezi.gradle.spaghetti.FQName
import com.prezi.gradle.spaghetti.ModuleConfiguration
import com.prezi.gradle.spaghetti.ModuleDefinition
import org.antlr.v4.runtime.misc.NotNull
import prezi.spaghetti.SpaghettiModuleParser

/**
 * Created by lptr on 16/11/13.
 */
class HaxeTypedefGeneratorVisitor extends AbstractHaxeGeneratorVisitor<Object> {

	private File moduleFile

	HaxeTypedefGeneratorVisitor(ModuleConfiguration config, ModuleDefinition module, File outputDirectory)
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

		moduleFile << """typedef ${moduleName} = {
"""
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
		generateTypeDefinition(ctx) { String typeName, FQName superType ->
			def declaration = "typedef ${typeName} = {"
			if (superType != null) {
				declaration += " > ${superType.fullyQualifiedName},"
			}
			return declaration
		}
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
