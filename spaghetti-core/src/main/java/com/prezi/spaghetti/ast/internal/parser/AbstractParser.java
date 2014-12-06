package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.Location;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

abstract class AbstractParser {
	protected final Locator locator;

	protected AbstractParser(Locator locator) {
		this.locator = locator;
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
}
