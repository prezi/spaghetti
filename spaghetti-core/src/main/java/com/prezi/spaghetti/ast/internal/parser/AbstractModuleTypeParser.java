package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.QualifiedTypeNode;
import org.antlr.v4.runtime.ParserRuleContext;

public abstract class AbstractModuleTypeParser<C extends ParserRuleContext, N extends QualifiedTypeNode> {
	private final C context;
	private final N node;

	protected AbstractModuleTypeParser(C context, N node) {
		this.context = context;
		this.node = node;
	}

	public abstract void parse(TypeResolver resolver);

	public final C getContext() {
		return context;
	}

	public final N getNode() {
		return node;
	}
}
