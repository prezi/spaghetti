package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleParser
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

	final methodTypeParams = []

	protected AbstractTypeScriptGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

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
		def result = "\t${ctx.name.text}${typeParamsResult}(${paramsResult}):${returnType};"
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
		return "${ctx.name.text}:${ ctx.type.accept(this) }"
	}

	@Override
	String visitCallbackTypeChain(@NotNull @NotNull ModuleParser.CallbackTypeChainContext ctx)
	{
		def elements = ctx.elements
		def lastType = elements.pop()
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
		def params = []
		elements.eachWithIndex { elem, index ->
			params.push("arg${index}: ${elem.accept(this)}")
		}
		return "(${params.join(", ")}) => ${retType}"
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
		if (methodTypeParams.contains(localTypeName))
		{
			return localTypeName
		}
		return module.resolveName(localTypeName)
	}
}
