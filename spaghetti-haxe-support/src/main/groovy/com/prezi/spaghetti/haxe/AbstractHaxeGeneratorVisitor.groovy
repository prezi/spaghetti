package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/11/13.
 */
abstract class AbstractHaxeGeneratorVisitor extends AbstractModuleVisitor<String> {
	private static final def PRIMITIVE_TYPES = [
			bool: "Bool",
			int: "Int",
			float: "Float",
			string: "String",
			any: "Dynamic"
	]

	protected AbstractHaxeGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	@Override
	String visitCallbackTypeChain(@NotNull @NotNull ModuleParser.CallbackTypeChainContext ctx)
	{
		return ctx.elements.collect { it.accept(this) }.join("->")
	}

	@Override
	String visitSubTypeChainElement(@NotNull @NotNull ModuleParser.SubTypeChainElementContext ctx)
	{
		return "(${ctx.typeChain().accept(this)})"
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
		def haxeType = fqTypeName.fullyQualifiedName
		if (ctx.arguments != null) {
			haxeType += ctx.arguments.accept(this)
		}
		return haxeType
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
		return "Void"
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
