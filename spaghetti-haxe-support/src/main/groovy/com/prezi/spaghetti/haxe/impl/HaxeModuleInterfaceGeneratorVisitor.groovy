package com.prezi.spaghetti.haxe.impl

import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.definition.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.haxe.AbstractHaxeMethodGeneratorVisitor
import com.prezi.spaghetti.haxe.WithDeprecation
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/11/13.
 */
class HaxeModuleInterfaceGeneratorVisitor extends AbstractHaxeMethodGeneratorVisitor {

	HaxeModuleInterfaceGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	@WithDeprecation
	@WithJavaDoc
	@Override
	String visitModuleDefinition(@NotNull @NotNull ModuleParser.ModuleDefinitionContext ctx)
	{
		return \
"""interface I${module.alias} {
${super.visitModuleDefinition(ctx)}
}
"""
	}

	@Override
	String visitModuleMethodDefinition(@NotNull ModuleParser.ModuleMethodDefinitionContext ctx) {
		if (ctx.isStatic) {
			return ""
		}
		return visitModuleMethodDefinitionInternal(ctx)
	}

	@WithDeprecation
	@WithJavaDoc
	String visitModuleMethodDefinitionInternal(@NotNull ModuleParser.ModuleMethodDefinitionContext ctx) {
		return visitChildren(ctx)
	}

	@Override
	String visitTypeDefinition(@NotNull @NotNull ModuleParser.TypeDefinitionContext ctx)
	{
		return ""
	}
}
