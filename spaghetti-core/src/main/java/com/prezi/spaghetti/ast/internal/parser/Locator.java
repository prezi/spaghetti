package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleDefinitionSource;
import com.prezi.spaghetti.ast.internal.DefaultLocation;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

public final class Locator {
	private final ModuleDefinitionSource source;

	public Locator(ModuleDefinitionSource source) {
		this.source = source;
	}

	public Location locate(Token token) {
		return new DefaultLocation(source, token.getLine(), token.getCharPositionInLine());
	}

	public Location locate(TerminalNode node) {
		return locate(node.getSymbol());
	}

	public Location locate(ParserRuleContext context) {
		return locate(context.start);
	}

	public ModuleDefinitionSource getSource() {
		return source;
	}
}
