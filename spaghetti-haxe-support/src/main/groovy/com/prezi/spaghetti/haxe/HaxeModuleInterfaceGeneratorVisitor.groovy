package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/11/13.
 */
class HaxeModuleInterfaceGeneratorVisitor extends AbstractHaxeMethodGeneratorVisitor {

	HaxeModuleInterfaceGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	@Override
	String visitModuleDefinition(@NotNull @NotNull ModuleParser.ModuleDefinitionContext ctx)
	{
		return ModuleUtils.formatDocumentation(ctx.documentation) +
"""interface ${module.name.localName} {
${super.visitModuleDefinition(ctx)}
}
"""
	}

	@Override
	String visitTypeDefinition(@NotNull @NotNull ModuleParser.TypeDefinitionContext ctx)
	{
		return ""
	}
}
