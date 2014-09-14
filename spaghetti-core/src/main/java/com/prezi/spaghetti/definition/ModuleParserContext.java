package com.prezi.spaghetti.definition;

import com.prezi.spaghetti.definition.internal.ParserErrorListener;
import com.prezi.spaghetti.grammar.ModuleLexer;
import com.prezi.spaghetti.grammar.ModuleParser;

public class ModuleParserContext {
	private final ModuleLexer lexer;
	private final ModuleParser parser;
	private final ParserErrorListener listener;

	public ModuleParserContext(ModuleLexer lexer, ModuleParser parser, ParserErrorListener listener) {
		this.lexer = lexer;
		this.parser = parser;
		this.listener = listener;
	}

	public ModuleLexer getLexer() {
		return lexer;
	}

	public ModuleParser getParser() {
		return parser;
	}

	public ParserErrorListener getListener() {
		return listener;
	}

}
