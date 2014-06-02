package com.prezi.spaghetti.ast.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

@SuppressWarnings("UnusedDeclaration")
public class InternalAstParserException extends RuntimeException {

	public InternalAstParserException(String message, Throwable cause) {
		super(message, cause);
	}

	private InternalAstParserException(Token start, Token stop, String message, Throwable cause) {
		this(createMessage(start, message), cause);
	}

	private static String createMessage(Token start, String message)
	{
		return " at line " + start.getLine() + ":" + start.getCharPositionInLine() + ": " + message;
	}

	public InternalAstParserException(Token token, String message, Throwable cause)
	{
		this(token, token, message, cause);
	}
	public InternalAstParserException(ParserRuleContext context, String message, Throwable cause)
	{
		this(context.start, context.stop, message, cause);
	}

	public InternalAstParserException(Token token, String message)
	{
		this(token, message, null);
	}
	public InternalAstParserException(Token token, Throwable cause)
	{
		this(token, null, cause);
	}

	public InternalAstParserException(ParserRuleContext context, String message)
	{
		this(context, message, null);
	}
	public InternalAstParserException(ParserRuleContext context, Throwable cause)
	{
		this(context, null, cause);
	}

}
