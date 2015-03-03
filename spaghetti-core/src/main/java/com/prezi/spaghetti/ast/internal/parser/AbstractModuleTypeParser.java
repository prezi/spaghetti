package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.QualifiedTypeNode;
import org.antlr.v4.runtime.ParserRuleContext;

public abstract class AbstractModuleTypeParser<C extends ParserRuleContext, N extends QualifiedTypeNode> extends AbstractParser<N> {
	private final C context;

	protected AbstractModuleTypeParser(Locator locator, C context, N node) {
		super(locator, node);
		this.context = context;
	}

	public final C getContext() {
		return context;
	}

	@Override
	public N getNode() {
		return super.getNode();
	}

	@Override
	public N parse(TypeResolver resolver) {
		parseInternal(resolver);
		return node;
	}

	abstract protected void parseInternal(TypeResolver resolver);
}
