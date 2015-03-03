package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.Location;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

abstract public class AbstractParser<N extends AstNode> {
	protected final Locator locator;
	protected final N node;

	protected AbstractParser(Locator locator, N node) {
		this.locator = locator;
		this.node = node;
	}

	protected Location locate(Token token) {
		return locator.locate(token);
	}

	protected Location locate(TerminalNode node) {
		return locator.locate(node);
	}

	protected Location locate(ParserRuleContext context) {
		return locator.locate(context);
	}

	public N getNode() {
		return node;
	}

	public abstract N parse(TypeResolver resolver);
}
