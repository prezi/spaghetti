package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.definition.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleBaseVisitor
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptEnumGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {
	protected TypeScriptEnumGeneratorVisitor(ModuleDefinition module) {
		super(module)
	}

	private class TypeScriptEnumValueGeneratorVisitor extends ModuleBaseVisitor<String> {
		private final int valueIndex

		TypeScriptEnumValueGeneratorVisitor(int valueIndex) {
			this.valueIndex = valueIndex
		}

		@WithJavaDoc
		@Override
		String visitEnumValue(@NotNull ModuleParser.EnumValueContext ctx) {
			return "\t${ctx.name.text} = ${valueIndex}"
		}
	}

	@WithJavaDoc
	@Override
	String visitEnumDefinition(@NotNull @NotNull ModuleParser.EnumDefinitionContext ctx)
	{
		def valueLines = []
		ctx.values.eachWithIndex{ valueCtx, index ->
			valueLines += valueCtx.accept(new TypeScriptEnumValueGeneratorVisitor(index))
		}
		return \
"""export enum ${ctx.name.text} {
${valueLines.join(",\n")}
}
"""
	}
}
