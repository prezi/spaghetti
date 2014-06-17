package com.prezi.spaghetti.definition

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ParserErrorListener extends BaseErrorListener {
	private static final Logger logger = LoggerFactory.getLogger(ParserErrorListener)
	private final String location
	private boolean inError

	ParserErrorListener(String location) {
		this.location = location
	}

	@Override
	public void syntaxError(Recognizer recognizer,
							Object offendingSymbol,
							int line,
							int charPositionInLine,
							String msg,
							RecognitionException ex) {
		logger.error "Syntax error in {} at line {}:{}: ${msg}", location, line, charPositionInLine
		logger.debug "    exception:", ex
		inError = true
	}

	boolean isInError() {
		return inError
	}
}
