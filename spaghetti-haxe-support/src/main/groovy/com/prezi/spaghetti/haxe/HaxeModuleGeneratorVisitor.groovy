package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/11/13.
 */
class HaxeModuleGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	private final Closure<String> defineType

	HaxeModuleGeneratorVisitor(ModuleDefinition module, Closure<String> defineType)
	{
		super(module)
		this.defineType = defineType
	}

	@Override
	String visitModuleDefinition(@NotNull @NotNull ModuleParser.ModuleDefinitionContext ctx)
	{
		return ModuleUtils.formatDocumentation(ctx.documentation) +
"""${defineType(module.name.localName)}
${super.visitModuleDefinition(ctx)}
}
"""
	}

	@Override
	String visitTypeDefinition(@NotNull @NotNull ModuleParser.TypeDefinitionContext ctx)
	{
		// Do not generate code for types
		return ""
	}
}
