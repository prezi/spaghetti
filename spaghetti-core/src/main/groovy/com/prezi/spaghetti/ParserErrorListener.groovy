package com.prezi.spaghetti

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer

/**
 * Created by lptr on 30/01/14.
 */
class ParserErrorListener extends BaseErrorListener {
	private final String location

	ParserErrorListener(String location) {
		this.location = location
	}

	@Override
	public void syntaxError(Recognizer recognizer,
							Object offendingSymbol,
							int line,
							int charPositionInLine,
							String msg,
							RecognitionException e)
	{
		System.err.println("Error in ${location} at line ${line}, column ${charPositionInLine} - ${msg}");
	}
}
