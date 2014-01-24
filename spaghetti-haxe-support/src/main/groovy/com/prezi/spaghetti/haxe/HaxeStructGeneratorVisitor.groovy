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
		return \
"""typedef ${ctx.name.text} = {
${ctx.propertyDefinition().collect {
	it.accept(this)
}.join("")}
}
"""
	}

	@WithJavaDoc
	@Override
	String visitPropertyDefinition(@NotNull @NotNull ModuleParser.PropertyDefinitionContext ctx)
	{
		def mutable = ModuleUtils.extractAnnotations(ctx.annotations()).containsKey("mutable")
		def modifiers = mutable ? "" : " (default, never)"
		return \
"""	var ${ctx.property.name.text}${modifiers}:${ctx.property.type.accept(this)};
"""
	}
}
