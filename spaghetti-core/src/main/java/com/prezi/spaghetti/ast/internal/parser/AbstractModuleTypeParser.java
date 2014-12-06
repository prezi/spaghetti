package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.QualifiedTypeNode;
import org.antlr.v4.runtime.ParserRuleContext;

public abstract class AbstractModuleTypeParser<C extends ParserRuleContext, N extends QualifiedTypeNode> extends AbstractParser {
	private final C context;
	private final N node;

	protected AbstractModuleTypeParser(Locator locator, C context, N node) {
		super(locator);
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
