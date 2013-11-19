package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.grammar.SpaghettiModuleBaseVisitor
import com.prezi.spaghetti.grammar.SpaghettiModuleParser
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.misc.NotNull
/**
 * Created by lptr on 16/11/13.
 */
abstract class AbstractHaxeGeneratorVisitor<T> extends SpaghettiModuleBaseVisitor<Object> {
	static def HAXE_TYPE_NAME_CONVERSION = [
			(ModuleConfiguration.TYPE_VOID): "Void",
			(ModuleConfiguration.TYPE_BOOL): "Bool",
			(ModuleConfiguration.TYPE_INT): "Int",
			(ModuleConfiguration.TYPE_FLOAT): "Float",
			(ModuleConfiguration.TYPE_STRING): "String",
			(ModuleConfiguration.TYPE_ANY): "Dynamic"
	]

	protected final ModuleConfiguration config
	protected final ModuleDefinition module
	protected final File outputDirectory

	protected File currentFile
	protected SpaghettiModuleParser.MethodParameterDefinitionContext lastMethodParameter

	protected AbstractHaxeGeneratorVisitor(ModuleConfiguration config, ModuleDefinition module, File outputDirectory)
	{
		this.config = config
		this.module = module
		this.outputDirectory = outputDirectory
	}

	protected File createHaxeSourceFile(String name)
	{
		return HaxeUtils.createHaxeSourceFile(name, module.name, outputDirectory)
	}

	protected T generateTypeDefinition(SpaghettiModuleParser.TypeDefinitionContext ctx, Closure<String> defineType)
	{
		def previousFile = currentFile
		def typeName = ctx.name.text
		FQName superType = null
		if (ctx.superType != null) {
			superType = module.name.resolveLocalName(FQName.fromContext(ctx.superType))
		}
		currentFile = createHaxeSourceFile(typeName)

		addDocumentationIfNecessary(ctx.documentation)
		currentFile << defineType(typeName, superType)
		currentFile << "\n"
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
		def paramType = haxeTypeName(ctx.type)
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
		def returnType = haxeTypeName(ctx.returnType)
		currentFile << "\tfunction ${methodName}("
		def result = super.visitMethodDefinition(ctx)
		currentFile << "):${returnType};\n"
		lastMethodParameter = null
		return result
	}

	protected String haxeTypeName(SpaghettiModuleParser.FqNameContext typeNameContext)
	{
		def typeName = FQName.fromContext(typeNameContext)
		def fqName = config.resolveTypeName(typeName, module.name)
		return HAXE_TYPE_NAME_CONVERSION.get(fqName) ?: fqName.fullyQualifiedName
	}

	protected void addDocumentationIfNecessary(Token doc)
	{
		def documentation = doc?.text
		if (documentation != null)
		{
			currentFile << documentation
		}
	}
}
