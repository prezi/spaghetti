package com.prezi.spaghetti.haxe.impl

import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.haxe.AbstractHaxeMethodGeneratorVisitor
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/11/13.
 */
class HaxeModuleStaticProxyGeneratorVisitor extends AbstractHaxeMethodGeneratorVisitor {

	HaxeModuleStaticProxyGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	@Override
	String visitModuleDefinition(@NotNull @NotNull ModuleParser.ModuleDefinitionContext ctx)
	{
		return \
"""@:final class __${module.alias}Static {
	public function new() {}
${super.visitModuleDefinition(ctx)}
}
"""
	}

	@Override
	String visitModuleMethodDefinition(@NotNull ModuleParser.ModuleMethodDefinitionContext ctx) {
		if (!ctx.isStatic) {
			return ""
		}
		return super.visitModuleMethodDefinition(ctx)
	}

	@Override
	String visitMethodDefinition(@NotNull @NotNull @NotNull @NotNull ModuleParser.MethodDefinitionContext ctx)
	{
		def returnType = ctx.returnTypeChain().accept(this)

		def params
		def callParams
		if (ctx.parameters) {
			params = ctx.parameters.accept(this)
			callParams = ctx.parameters.elements.collect { it.name.text }.join(", ")
		} else {
			params = ""
			callParams = ""
		}

		return \
"""	public function ${ctx.name.text}(${params}):${returnType} {
		${returnType == "Void"?"":"return "}${module.name}.${module.alias}.${ctx.name.text}(${callParams});
	}
"""
	}

	@Override
	String visitTypeDefinition(@NotNull @NotNull ModuleParser.TypeDefinitionContext ctx)
	{
		return ""
	}
}
