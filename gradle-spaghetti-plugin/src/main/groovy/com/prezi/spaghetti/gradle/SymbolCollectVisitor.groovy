package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.misc.NotNull

import java.util.Set

class SymbolCollectVisitor extends AbstractModuleVisitor<Set<String>> {

	public SymbolCollectVisitor(ModuleDefinition module)
	{
		super(module);
	}

	@Override
	protected Set<String> aggregateResult(Set<String> aggregate, Set<String> nextResult) {
		return aggregate + nextResult;
	}

	@Override
	protected Set<String> defaultResult() {
		return [];
	}

	@Override
	public Set<String> visitExternTypeDefinition(@NotNull ModuleParser.ExternTypeDefinitionContext ctx) {

		ctx.name.parts.collect{it.getText()};
	}

	@Override
	public Set<String> visitMethodDefinition(@NotNull @NotNull ModuleParser.MethodDefinitionContext ctx){

		return [ctx.name.getText()];
	}

	@Override
	public Set<String> visitStructDefinition(@NotNull ModuleParser.StructDefinitionContext ctx) {

		return ctx.propertyDefinition().collect{it.property.name.getText()};
	}

	@Override
	public Set<String> visitConstDefinition(@NotNull ModuleParser.ConstDefinitionContext ctx) {

		return [ctx.name.getText()] + ctx.propertyDefinition().collect{it.property.name.getText()};
	}

}
