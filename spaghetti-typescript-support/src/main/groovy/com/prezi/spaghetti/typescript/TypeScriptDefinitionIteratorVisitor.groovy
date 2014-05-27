package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.definition.AbstractModuleVisitor
import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 20/11/13.
 */
class TypeScriptDefinitionIteratorVisitor extends AbstractModuleVisitor<String> {

	TypeScriptDefinitionIteratorVisitor(ModuleDefinition module) {
		super(module)
	}

	@Override
	String visitInterfaceDefinition(@NotNull @NotNull ModuleParser.InterfaceDefinitionContext ctx)
	{
		return new TypeScriptInterfaceGeneratorVisitor(module).visit(ctx)
	}

	@Override
	String visitEnumDefinition(@NotNull @NotNull ModuleParser.EnumDefinitionContext ctx)
	{
		return new TypeScriptEnumGeneratorVisitor(module).visit(ctx)
	}

	@Override
	String visitStructDefinition(@NotNull @NotNull ModuleParser.StructDefinitionContext ctx)
	{
		return new TypeScriptStructGeneratorVisitor(module).visit(ctx)
	}

	@Override
	String visitConstDefinition(@NotNull @NotNull ModuleParser.ConstDefinitionContext ctx)
	{
		return new TypeScriptConstGeneratorVisitor(module).visit(ctx)
	}

	@Override
	protected String aggregateResult(String aggregate, String nextResult) {
		return aggregate + nextResult
	}

	@Override
	protected String defaultResult() {
		return ""
	}
}
