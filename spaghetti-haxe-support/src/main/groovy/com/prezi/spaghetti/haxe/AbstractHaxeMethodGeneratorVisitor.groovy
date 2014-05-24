package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.definition.FQName
import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.definition.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleParser
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

	@WithDeprecation
	@WithJavaDoc
	@Override
	String visitMethodDefinition(@NotNull @NotNull ModuleParser.MethodDefinitionContext ctx)
	{
		def typeParams = ctx.typeParameters()
		typeParams?.parameters?.each { param ->
			methodTypeParams.add(FQName.fromString(param.name.text))
		}
		def returnType = ctx.returnTypeChain().accept(this)

		def typeParamsResult = typeParams?.accept(this) ?: ""
		def paramsResult = ctx.parameters?.accept(this) ?: ""
		def name = ctx.name.text

		def result = \
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
		return ctx.name.text + ":" + ctx.type.accept(this)
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
