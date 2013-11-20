package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.grammar.SpaghettiModuleParser
import org.antlr.v4.runtime.Token
/**
 * Created by lptr on 16/11/13.
 */
abstract class AbstractHaxeGeneratorVisitor extends AbstractModuleVisitor<String> {
	static def HAXE_TYPE_NAME_CONVERSION = [
			(ModuleConfiguration.TYPE_VOID): "Void",
			(ModuleConfiguration.TYPE_BOOL): "Bool",
			(ModuleConfiguration.TYPE_INT): "Int",
			(ModuleConfiguration.TYPE_FLOAT): "Float",
			(ModuleConfiguration.TYPE_STRING): "String",
			(ModuleConfiguration.TYPE_ANY): "Dynamic"
	]

	protected final ModuleConfiguration config

	protected AbstractHaxeGeneratorVisitor(ModuleConfiguration config, ModuleDefinition module)
	{
		super(module)
		this.config = config
	}

	protected String generateTypeDefinition(SpaghettiModuleParser.TypeDefinitionContext ctx, Closure<String> defineType)
	{
		def typeName = ctx.name.text
		FQName superType = null
		if (ctx.superType != null) {
			superType = module.name.resolveLocalName(FQName.fromContext(ctx.superType))
		}

		String result = addDocumentationIfNecessary(ctx.documentation) \
			+ """${defineType(typeName, superType)}

${super.visitTypeDefinition(ctx)}
}
"""
		return result
	}

	protected String generateMethodParameterDefinition(SpaghettiModuleParser.MethodParameterDefinitionContext ctx)
	{
		def paramName = ctx.name.text
		def paramType = haxeTypeName(ctx.type)
		return "${paramName}:${paramType}"
	}

	protected String generateMethodDefinition(SpaghettiModuleParser.MethodDefinitionContext ctx)
	{
		String result = addDocumentationIfNecessary(ctx.documentation)
		def methodName = ctx.name.text
		def returnType = haxeTypeName(ctx.returnType)
		result += "\tfunction ${methodName}("
		result += ctx.params.collect { paramCtx ->
			generateMethodParameterDefinition(paramCtx)
		}.join(", ")
		result += "):${returnType};\n"
		return result
	}

	protected String haxeTypeName(SpaghettiModuleParser.FqNameContext typeNameContext)
	{
		def localTypeName = FQName.fromContext(typeNameContext)
		def fqTypeName = config.resolveTypeName(localTypeName, module.name)
		return HAXE_TYPE_NAME_CONVERSION.get(fqTypeName) ?: fqTypeName.fullyQualifiedName
	}

	protected static String addDocumentationIfNecessary(Token doc)
	{
		return doc?.text ?: ""
	}

	@Override
	protected String aggregateResult(String aggregate, String nextResult) {
		return aggregate + nextResult
	}

	@Override
	protected String defaultResult() {
		return ""
	}

}
