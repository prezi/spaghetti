package com.prezi.spaghetti.definition.internal;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserErrorListener extends BaseErrorListener {
	public ParserErrorListener(String location) {
		this.location = location;
	}

	@Override
	public void syntaxError(Recognizer recognizer, Object offendingSymbol, int line, int charPositionInLine, final String msg, RecognitionException ex) {
		logger.error("Syntax error in {} at line {}:{}: " + msg, location, line, charPositionInLine);
		logger.debug("    exception:", ex);
		inError = true;
	}

	public boolean isInError() {
		return inError;
	}

	private static final Logger logger = LoggerFactory.getLogger(ParserErrorListener.class);
	private final String location;
	private boolean inError;
}
