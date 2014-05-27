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

	@WithDeprecation
	@WithJavaDoc
	@Override
	String visitStructDefinition(@NotNull @NotNull ModuleParser.StructDefinitionContext ctx)
	{
"""typedef ${ctx.name.text} = {
${visitChildren(ctx)}
}
"""
	}

	@WithDeprecation
	@WithJavaDoc
	@Override
	String visitPropertyDefinition(@NotNull @NotNull ModuleParser.PropertyDefinitionContext ctx)
	{
		def annotations = ctx.annotations()
		def mutable = ModuleUtils.extractAnnotations(annotations).containsKey("mutable")
		def modifiers = mutable ? "" : " (default, never)"
"""	var ${ctx.property.name.text}${modifiers}:${ctx.property.type.accept(this)};
"""
	}
}
