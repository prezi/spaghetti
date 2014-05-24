package com.prezi.spaghetti.typescript.impl

import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.definition.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptModuleInterfaceGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	TypeScriptModuleInterfaceGeneratorVisitor(ModuleDefinition module) {
		super(module)
	}

	@WithJavaDoc
	@Override
	String visitModuleDefinition(@NotNull ModuleParser.ModuleDefinitionContext ctx) {
"""export interface I${module.alias} {
${visitChildren(ctx)}
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

	@WithJavaDoc
	String visitModuleMethodDefinitionInternal(@NotNull ModuleParser.ModuleMethodDefinitionContext ctx) {
		return visitChildren(ctx)
	}

	@Override
	String visitTypeDefinition(@NotNull ModuleParser.TypeDefinitionContext ctx) {
		return ""
	}
}
