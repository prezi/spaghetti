package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.grammar.ModuleParser.AnnotationsContext
import org.antlr.v4.runtime.Token
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
		def result = generateMethod(ctx.documentation, typeParams, returnType, ctx.name.text, {
			ctx.parameters?.accept(this) ?: ""
		})
		methodTypeParams.clear()
		return result
	}

	@Override
	String visitPropertyDefinition(@NotNull @NotNull ModuleParser.PropertyDefinitionContext ctx)
	{
		def propertyName = ctx.property.name.text.capitalize()
		def propertyType = ctx.property.type
		def resolvedPropertyType = propertyType.accept(this)

		def result = generateMethod(ctx.documentation, null, resolvedPropertyType, "get" + propertyName, { "" })
		result += generateMethod(ctx.documentation, null, "Void", "set" + propertyName, { ctx.property.accept(this) })
		return result
	}

	private String generateMethod(Token doc,
										 ModuleParser.TypeParametersContext typeParameters,
										 String resolvedReturnType,
										 String name,
										 Closure<String> generateParams)
	{
		def docResult = ModuleUtils.formatDocumentation(doc, "\t")
		def typeParamsResult = typeParameters?.accept(this) ?: ""
	return """	${docResult}
	function ${name}${typeParamsResult}(${generateParams()}):${resolvedReturnType};
"""
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
