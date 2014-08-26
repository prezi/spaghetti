package com.prezi.spaghetti.ast.parser;

import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import org.antlr.v4.runtime.Token;

public class ParseUtils {
	public static String createWarning(ModuleDefinitionSource source, Token token, String message) {
		return "in " + source.getLocation() + " at " + token.getLine() + ":" + token.getCharPositionInLine() + ": " + message;
	}
}
