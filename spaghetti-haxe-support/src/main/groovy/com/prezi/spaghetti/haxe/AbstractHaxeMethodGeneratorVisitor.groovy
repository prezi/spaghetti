package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.grammar.ModuleParser.AnnotationsContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 29/11/13.
 */
class AbstractHaxeMethodGeneratorVisitor extends AbstractHaxeGeneratorVisitor {
	protected AbstractHaxeMethodGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	@Override
	String visitMethodDefinition(@NotNull @NotNull ModuleParser.MethodDefinitionContext ctx)
	{
		def returnType = ctx.returnTypeChain().accept(this)
		returnType = wrapNullable(ctx.annotations(), returnType)
		return generateMethod(ctx.documentation, returnType, ctx.name.text, {
			ctx.parameters?.accept(this) ?: ""
		})
	}

	@Override
	String visitPropertyDefinition(@NotNull @NotNull ModuleParser.PropertyDefinitionContext ctx)
	{
		def propertyName = ctx.property.name.text.capitalize()
		def propertyType = ctx.property.type
		def resolvedPropertyType = propertyType.accept(this)

		def result = generateMethod(ctx.documentation, resolvedPropertyType, "get" + propertyName, { "" })
		result += generateMethod(ctx.documentation, "Void", "set" + propertyName, { ctx.property.accept(this) })
		return result
	}

	private static String generateMethod(Token doc,
								  String resolvedReturnType,
								  String name,
								  Closure<String> generateParams) {
		return ModuleUtils.formatDocumentation(doc, "\t") +
"""	function ${name}(${generateParams()}):${resolvedReturnType};
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

	private static String wrapNullable(AnnotationsContext annotationsContext, String type) {
		def annotations = ModuleUtils.extractAnnotations(annotationsContext)
		boolean nullable = annotations.containsKey("nullable")
		return nullable ? "Null<${type}>" : type
	}
}
