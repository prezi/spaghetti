package com.prezi.gradle.spaghetti.haxe

import com.prezi.gradle.spaghetti.ModuleConfiguration
import com.prezi.gradle.spaghetti.ModuleDefinition
import org.antlr.v4.runtime.misc.NotNull
import prezi.spaghetti.SpaghettiModuleParser

/**
 * Created by lptr on 16/11/13.
 */
class HaxeTypedefGeneratorVisitor extends HaxeDefinitionGeneratorVisitor<Object> {

	private File moduleFile
	private MethodGenerationMode methodGenerationMode
	private MethodParameterGenerationMode methodParameterGenerationMode

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

		methodGenerationMode = MethodGenerationMode.MODULE
		moduleFile << """@:final class ${moduleName} {
	var module:Dynamic;

	public function new(module:Dynamic) {
		this.module = module;
	}

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
		def previousMode = methodGenerationMode
		methodGenerationMode = MethodGenerationMode.TYPE
		def result = generateTypeDefinition(ctx) { String typeName -> "typedef ${typeName} =" }
		methodGenerationMode = previousMode
		return result
	}

	@Override
	Object visitMethodDefinition(@NotNull @NotNull SpaghettiModuleParser.MethodDefinitionContext ctx)
	{
		methodParameterGenerationMode = MethodParameterGenerationMode.DECLARATION
		if (methodGenerationMode == MethodGenerationMode.TYPE) {
			return generateMethodDefinition(ctx)
		} else {
			addDocumentationIfNecessary(ctx.documentation)
			def methodName = ctx.name.text
			def returnType = haxeTypeName(ctx.returnType.text)
			currentFile << "\tpublic function ${methodName}("
			super.visitMethodDefinition(ctx)
			currentFile << "):${returnType} {\n"
			currentFile << "\t\tmodule.${methodName}("
			methodParameterGenerationMode = MethodParameterGenerationMode.CALL
			super.visitMethodDefinition(ctx)
			methodParameterGenerationMode = MethodParameterGenerationMode.DECLARATION
			currentFile << ");\n"
			currentFile << "\t}\n"
			currentFile << "\n"
			return null
		}
	}

	@Override
	Object visitMethodParameterDefinition(@NotNull @NotNull SpaghettiModuleParser.MethodParameterDefinitionContext ctx)
	{
		return generateMethodParameter(ctx, methodParameterGenerationMode == MethodParameterGenerationMode.DECLARATION)
	}

	enum MethodGenerationMode {
		TYPE,
		MODULE
	}

	enum MethodParameterGenerationMode {
		DECLARATION,
		CALL
	}
}
