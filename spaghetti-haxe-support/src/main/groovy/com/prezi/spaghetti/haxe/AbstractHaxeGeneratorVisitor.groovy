package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.grammar.SpaghettiModuleParser
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.misc.NotNull
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

	protected String resolveHaxeType(SpaghettiModuleParser.FqNameContext typeNameContext)
	{
		def localTypeName = FQName.fromContext(typeNameContext)
		def fqTypeName = config.resolveTypeName(localTypeName, module.name)
		return HAXE_TYPE_NAME_CONVERSION.get(fqTypeName) ?: fqTypeName.fullyQualifiedName
	}

	protected static String extractDocumentation(Token doc)
	{
		return doc?.text ?: ""
	}

	@Override
	String visitMethodDefinition(@NotNull @NotNull SpaghettiModuleParser.MethodDefinitionContext ctx)
	{
		def methodName = ctx.name.text
		def returnType = resolveHaxeType(ctx.returnType)

		String result = extractDocumentation(ctx.documentation)
		result += "\tfunction ${methodName}("
		result += ctx.params.collect { paramCtx ->
			def paramName = paramCtx.name.text
			def paramType = resolveHaxeType(paramCtx.type)
			return "${paramName}:${paramType}"
		}.join(", ")
		result += "):${returnType};\n"
		return result
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
