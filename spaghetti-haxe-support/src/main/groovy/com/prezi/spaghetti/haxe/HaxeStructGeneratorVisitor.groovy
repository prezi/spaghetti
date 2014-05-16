package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.definition.ModuleUtils
import com.prezi.spaghetti.definition.WithJavaDoc
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
		def result = Deprecation.annotationFromCxt(Type.StructName, ctx.name.text, ctx.annotations())

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
		def annotations = ctx.annotations()
		def mutable = ModuleUtils.extractAnnotations(annotations).containsKey("mutable")
		def modifiers = mutable ? "" : " (default, never)"
		def result = Deprecation.annotationFromCxt(Type.StructField, ctx.property.name.text, annotations) + "\n"

		result += \
"""	var ${ctx.property.name.text}${modifiers}:${ctx.property.type.accept(this)};
"""
		return result
	}
}
