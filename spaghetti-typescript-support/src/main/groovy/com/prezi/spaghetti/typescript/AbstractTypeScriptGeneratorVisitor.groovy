package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/11/13.
 */
abstract class AbstractTypeScriptGeneratorVisitor extends AbstractModuleVisitor<String> {
	private static final def PRIMITIVE_TYPES = [
			bool: "boolean",
			int: "number",
			float: "number",
			string: "string",
			any: "any"
	]

	protected AbstractTypeScriptGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	@Override
	String visitMethodDefinition(@NotNull @NotNull ModuleParser.MethodDefinitionContext ctx)
	{
		def returnType = ctx.returnTypeChain().accept(this)
		return generateMethod(ctx.documentation, returnType, ctx.name.text, {
			ctx.parameters != null ? ctx.parameters.accept(this) : ""
		})
	}

	@Override
	String visitPropertyDefinition(@NotNull @NotNull ModuleParser.PropertyDefinitionContext ctx)
	{
		def propertyName = TypeScriptUtils.capitalize(ctx.property.name.text)
		def propertyType = ctx.property.type
		def resolvedPropertyType = propertyType.accept(this)

		def result = generateMethod(ctx.documentation, resolvedPropertyType, "get" + propertyName, { "" })
		result += generateMethod(ctx.documentation, "void", "set" + propertyName, { ctx.property.accept(this) })
		return result
	}

	private static String generateMethod(Token doc,
								  String resolvedReturnType,
								  String name,
								  Closure<String> generateParams) {
		return ModuleUtils.formatDocumentation(doc, "\t") +
"""	${name}(${generateParams()}):${resolvedReturnType};
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
		return "${ctx.name.text}:${ ctx.type.accept(this) }"
	}

	@Override
	String visitCallbackTypeChain(@NotNull @NotNull ModuleParser.CallbackTypeChainContext ctx)
	{
		def elements = ctx.elements
		def lastType = elements.pop()

		if (elements.empty) {
			return lastType.accept(this);
		}
		else {
			def retType = lastType.accept(this);
			if (elements.size() == 1)
			{
				def singleElement = elements.get(0)
				if (singleElement instanceof ModuleParser.SimpleTypeChainElementContext)
				{
					if (singleElement.returnType().voidType() != null)
					{
						return "() => ${retType}"
					}
				}
			}
			def params = elements.collect { elem -> elem.accept(this) }
			return "(${params.join(", ")}) => ${retType}"
		}
	}

	@Override
	String visitSubTypeChainElement(@NotNull @NotNull ModuleParser.SubTypeChainElementContext ctx)
	{
		return "Function"
	}

	@Override
	String visitValueType(@NotNull @NotNull ModuleParser.ValueTypeContext ctx)
	{
		String type = ctx.getChild(0).accept(this)
		ctx.ArrayQualifier().each { type = "Array<${type}>" }
		return type
	}

	@Override
	String visitModuleType(@NotNull @NotNull ModuleParser.ModuleTypeContext ctx)
	{
		def localTypeName = FQName.fromContext(ctx.name)
		def fqTypeName = resolveName(localTypeName)
		def typeScriptType = fqTypeName.fullyQualifiedName
		if (ctx.arguments != null) {
			typeScriptType += ctx.arguments.accept(this)
		}
		return typeScriptType
	}

	@Override
	String visitTypeParameters(@NotNull @NotNull ModuleParser.TypeParametersContext ctx)
	{
		return "<" + ctx.parameters.collect { param ->
			param.accept(this)
		}.join(", ") + ">"
	}

	@Override
	String visitTypeParameter(@NotNull @NotNull ModuleParser.TypeParameterContext ctx)
	{
		return ctx.name.text
	}

	@Override
	String visitTypeArguments(@NotNull @NotNull ModuleParser.TypeArgumentsContext ctx)
	{
		return "<" + ctx.arguments.collect { arg ->
			arg.accept(this)
		}.join(", ") + ">"
	}

	@Override
	String visitVoidType(@NotNull @NotNull ModuleParser.VoidTypeContext ctx)
	{
		return "void"
	}

	@Override
	String visitPrimitiveType(@NotNull @NotNull ModuleParser.PrimitiveTypeContext ctx)
	{
		return PRIMITIVE_TYPES.get(ctx.text)
	}

	@Override
	protected String aggregateResult(String aggregate, String nextResult) {
		return aggregate + nextResult
	}

	@Override
	protected String defaultResult() {
		return ""
	}

	protected FQName resolveName(FQName localTypeName)
	{
		return module.resolveName(localTypeName)
	}
}
