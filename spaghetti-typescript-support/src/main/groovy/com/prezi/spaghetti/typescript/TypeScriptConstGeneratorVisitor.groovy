package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.definition.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptConstGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {
	protected TypeScriptConstGeneratorVisitor(ModuleDefinition module) {
		super(module)
	}

	@WithJavaDoc
	@Override
	String visitConstDefinition(@NotNull @NotNull ModuleParser.ConstDefinitionContext ctx)
	{
"""export class ${ctx.name.text} {
${visitChildren(ctx)}
}
"""
	}

	@WithJavaDoc
	@Override
	String visitConstEntry(@NotNull ModuleParser.ConstEntryContext ctx) {
		return super.visitConstEntry(ctx)
	}

	@Override
	String visitConstEntryDecl(@NotNull ModuleParser.ConstEntryDeclContext ctx) {
		String type
		Token value
		if (ctx.boolValue) {
			type = "boolean"
			value = ctx.boolValue
		} else if (ctx.intValue) {
			type = "number"
			value = ctx.intValue
		} else if (ctx.floatValue) {
			type = "number"
			value = ctx.floatValue
		} else if (ctx.stringValue) {
			type = "string"
			value = ctx.stringValue
		} else {
			throw new IllegalArgumentException("Unknown constant type: " + ctx.dump())
		}
		return "\tstatic ${ctx.name.text}: ${type} = ${value.text};\n"
	}
}
