package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.TypeNode
import org.antlr.v4.runtime.ParserRuleContext

abstract class AbstractModuleTypeParser<C extends ParserRuleContext, N extends TypeNode> {
	final C context
	final N node

	protected AbstractModuleTypeParser(C context, N node) {
		this.context = context
		this.node = node
	}

	abstract void parse(TypeResolver resolver)
}
