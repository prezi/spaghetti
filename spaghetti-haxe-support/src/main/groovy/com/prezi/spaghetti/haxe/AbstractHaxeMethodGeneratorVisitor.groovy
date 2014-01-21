package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.grammar.ModuleParser.AnnotationsContext
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 29/11/13.
 */
abstract class AbstractHaxeMethodGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	final methodTypeParams = []

	protected AbstractHaxeMethodGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	@Override
	String visitMethodDefinition(@NotNull @NotNull ModuleParser.MethodDefinitionContext ctx)
	{
		def typeParams = ctx.typeParameters()
		typeParams?.parameters?.each { param ->
			methodTypeParams.add(FQName.fromString(param.name.text))
		}
		def returnType = ctx.returnTypeChain().accept(this)
		returnType = wrapNullable(ctx.annotations(), returnType)

		def docResult = ModuleUtils.formatDocumentation(ctx.documentation, "\t")
		def typeParamsResult = typeParams?.accept(this) ?: ""
		def paramsResult = ctx.parameters?.accept(this) ?: ""
		def name = ctx.name.text
		def result = docResult +
"""	function ${name}${typeParamsResult}(${paramsResult}):${returnType};
"""
		methodTypeParams.clear()
		return result
	}

	@Override
	String visitTypeNamePairs(@NotNull @NotNull ModuleParser.TypeNamePairsContext ctx)
	{
		return ctx.elements.collect { elementCtx ->
			elementCtx.accept(this)
		}.join(", ")
	}

	@Override
	String visitTypeNamePair(@NotNull @NotNull ModuleParser.TypeNamePairContext ctx)
	{
		def result = ctx.name.text + ":"
		def type = ctx.type.accept(this)
		result += wrapNullable(ctx.annotations(), type)
		return result
	}

	protected static String wrapNullable(AnnotationsContext annotationsContext, String type) {
		def annotations = ModuleUtils.extractAnnotations(annotationsContext)
		boolean nullable = annotations.containsKey("nullable")
		return nullable ? "Null<${type}>" : type
	}

	@Override
	protected FQName resolveName(FQName localTypeName)
	{
		if (methodTypeParams.contains(localTypeName))
		{
			return localTypeName
		}
		return super.resolveName(localTypeName)
	}
}
