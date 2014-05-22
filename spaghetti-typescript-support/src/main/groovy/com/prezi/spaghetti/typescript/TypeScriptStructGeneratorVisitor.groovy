package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.definition.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptStructGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	protected TypeScriptStructGeneratorVisitor(ModuleDefinition module) {
		super(module)
	}

	@WithJavaDoc
	@Override
	String visitStructDefinition(@NotNull ModuleParser.StructDefinitionContext ctx) {
"""export interface ${ctx.name.text} {
${visitChildren(ctx)}
}
"""
	}

	@WithJavaDoc
	@Override
	String visitPropertyDefinition(@NotNull ModuleParser.PropertyDefinitionContext ctx) {
"""	${ctx.property.name.text}: ${ctx.property.type.accept(this)};
"""
	}
}
