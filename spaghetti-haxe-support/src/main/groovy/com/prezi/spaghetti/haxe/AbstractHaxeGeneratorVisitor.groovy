package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.misc.NotNull
/**
 * Created by lptr on 16/11/13.
 */
abstract class AbstractHaxeGeneratorVisitor extends AbstractModuleVisitor<String> {
	private static final def PRIMITIVE_TYPES = [
			bool: "Bool",
			int: "Int",
			float: "Float",
			String: "String",
			any: "Dynamic"
	]

	protected final ModuleConfiguration config

	protected AbstractHaxeGeneratorVisitor(ModuleConfiguration config, ModuleDefinition module)
	{
		super(module)
		this.config = config
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
		def propertyName = HaxeUtils.capitalize(ctx.property.name.text)
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
		return "${ctx.name.text}:${ ctx.type.accept(this) }"
	}

	@Override
	String visitNormalTypeChain(@NotNull @NotNull ModuleParser.NormalTypeChainContext ctx)
	{
		return ctx.returnType().collect { it.accept(this) }.join("->")
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
		def fqTypeName = config.resolveTypeName(localTypeName, module.name)
		def haxeType = fqTypeName.fullyQualifiedName
		return haxeType
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
}
