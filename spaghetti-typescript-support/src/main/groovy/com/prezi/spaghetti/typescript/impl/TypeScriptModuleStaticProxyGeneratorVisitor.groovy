package com.prezi.spaghetti.typescript.impl

import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/11/13.
 */
class TypeScriptModuleStaticProxyGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	TypeScriptModuleStaticProxyGeneratorVisitor(ModuleDefinition module)
	{
		super(module)
	}

	@Override
	String visitModuleDefinition(@NotNull @NotNull ModuleParser.ModuleDefinitionContext ctx)
	{
		return \
"""export class __${module.alias}Static {
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
	protected String visitMethodDefinitionInternal(@NotNull @NotNull ModuleParser.MethodDefinitionContext ctx)
	{
		def returnType = ctx.returnTypeChain().accept(this)

		def typeParams = ctx.typeParameters()?.accept(this) ?: ""
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
"""	${ctx.name.text}${typeParams}(${params}):${returnType} {
		${returnType == "void"?"":"return "}${module.name}.${module.alias}.${ctx.name.text}(${callParams});
	}
"""
	}

	@Override
	String visitTypeDefinition(@NotNull @NotNull ModuleParser.TypeDefinitionContext ctx)
	{
		return ""
	}
}
