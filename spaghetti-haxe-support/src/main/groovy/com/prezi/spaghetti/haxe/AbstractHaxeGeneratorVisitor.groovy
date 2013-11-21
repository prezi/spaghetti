package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.SpaghettiModuleParser
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.misc.NotNull
/**
 * Created by lptr on 16/11/13.
 */
abstract class AbstractHaxeGeneratorVisitor extends AbstractModuleVisitor<String> {
	static Map<FQName, String> HAXE_TYPE_NAME_CONVERSION = [
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

	@Override
	String visitMethodDefinition(@NotNull @NotNull SpaghettiModuleParser.MethodDefinitionContext ctx)
	{
		def returnType = resolveHaxeType(ctx.returnType)
		return generateMethod(ctx.documentation, returnType, ctx.name.text, ctx.params)
	}

	@Override
	String visitPropertyDefinition(@NotNull @NotNull SpaghettiModuleParser.PropertyDefinitionContext ctx)
	{
		def propertyName = HaxeUtils.capitalize(ctx.property.name.text)
		def propertyType = ctx.property.type
		def resolvedPropertyType = resolveHaxeType(propertyType)

		def result = generateMethod(ctx.documentation, resolvedPropertyType, "get" + propertyName, [])
		result += generateMethod(ctx.documentation, "Void", "set" + propertyName, [ ctx.property ])
		return result
	}

	private String generateMethod(Token doc,
								  String resolvedReturnType,
								  String name,
								  Iterable<SpaghettiModuleParser.TypedNameContext> params) {
		return ModuleUtils.formatDocumentation(doc, "\t") +
"""	function ${name}(${generateParameters(params)}):${resolvedReturnType};
"""
	}

	private String generateParameters(Iterable<SpaghettiModuleParser.TypedNameContext> params) {
		return params.collect { paramCtx ->
			def paramName = paramCtx.name.text
			def paramType = resolveHaxeType(paramCtx.type)
			return "${paramName}:${paramType}"
		}.join(", ")
	}

	protected String resolveHaxeType(SpaghettiModuleParser.ValueTypeContext valueTypeContext)
	{
		def localTypeName = FQName.fromContext(valueTypeContext.name)
		def fqTypeName = config.resolveTypeName(localTypeName, module.name)
		def haxeTypeName = HAXE_TYPE_NAME_CONVERSION.get(fqTypeName) ?: fqTypeName.fullyQualifiedName
		String haxeType = haxeTypeName
		valueTypeContext.arrayDimensions.each { haxeType = "Array<${haxeType}>" }
		return haxeType
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
