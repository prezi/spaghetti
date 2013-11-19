package com.prezi.gradle.spaghetti.haxe

import com.prezi.gradle.spaghetti.ModuleConfiguration
import com.prezi.gradle.spaghetti.ModuleDefinition
import org.antlr.v4.runtime.misc.NotNull
import prezi.spaghetti.SpaghettiModuleParser

/**
 * Created by lptr on 16/11/13.
 */
class HaxeInterfaceGeneratorVisitor extends AbstractHaxeGeneratorVisitor<Object> {

	HaxeInterfaceGeneratorVisitor(ModuleConfiguration config, ModuleDefinition module, File outputDirectory)
	{
		super(config, module, outputDirectory)
	}

	@Override
	Object visitModuleDefinition(@NotNull @NotNull SpaghettiModuleParser.ModuleDefinitionContext ctx)
	{
		def moduleName = module.name.localName
		File moduleFile = createHaxeSourceFile(moduleName)
		currentFile = moduleFile

		addDocumentationIfNecessary(ctx.documentation)

		moduleFile << "interface ${moduleName} {\n"
		def result = super.visitModuleDefinition(ctx);
		moduleFile << "}\n"

		// Make sure nothing nasty happens later
		currentFile = null

		def initializerName = "__" + moduleName + "Init"
		File initFile = createHaxeSourceFile(initializerName)
		initFile << """class ${initializerName} {
	public static function __init__() {
		untyped __module = new ${moduleName}Impl();
	}
}
"""

		return result
	}

	@Override
	Object visitTypeDefinition(@NotNull @NotNull SpaghettiModuleParser.TypeDefinitionContext ctx)
	{
		return generateTypeDefinition(ctx) { String typeName -> "interface ${typeName}" }
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
