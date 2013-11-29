package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleBaseVisitor
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

	@Override
	String visitStructDefinition(@NotNull @NotNull ModuleParser.StructDefinitionContext ctx)
	{
		return ModuleUtils.formatDocumentation(ctx.documentation) +
"""typedef ${ctx.name.text} = {
${ctx.propertyDefinition().collect {
	it.accept(this)
}.join("")}
}
"""
	}

	@Override
	String visitPropertyDefinition(@NotNull @NotNull ModuleParser.PropertyDefinitionContext ctx)
	{
		return ModuleUtils.formatDocumentation(ctx.documentation, "\t") +
"""	var ${ctx.property.name.text}:${ctx.property.type.accept(this)};
"""
	}
}
