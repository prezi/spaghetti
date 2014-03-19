package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull
/**
 * Created by lptr on 16/11/13.
 */
class HaxeStructGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	protected HaxeStructGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	@WithJavaDoc
	@Override
	String visitStructDefinition(@NotNull @NotNull ModuleParser.StructDefinitionContext ctx)
	{
		def result = ""

		def deprecatedAnn = ModuleUtils.extractAnnotations(ctx.annotations())["deprecated"]
		if (deprecatedAnn != null) {
			result += Deprecation.annotation(Type.StructName, ctx.name.text, deprecatedAnn) + "\n"
		}

		result += \
"""typedef ${ctx.name.text} = {
${ctx.propertyDefinition().collect {
	it.accept(this)
}.join("")}
}
"""
		return result
	}

	@WithJavaDoc
	@Override
	String visitPropertyDefinition(@NotNull @NotNull ModuleParser.PropertyDefinitionContext ctx)
	{
		def annMap = ModuleUtils.extractAnnotations(ctx.annotations())
		def mutable = annMap.containsKey("mutable")
		def modifiers = mutable ? "" : " (default, never)"
		def result = ""

		def deprecatedAnn = annMap["deprecated"]
		if (deprecatedAnn != null) {
			result += Deprecation.annotation(Type.StructField, ctx.property.name.text, deprecatedAnn) + "\n"
		}

		result += \
"""	var ${ctx.property.name.text}${modifiers}:${ctx.property.type.accept(this)};
"""
		return result
	}
}
