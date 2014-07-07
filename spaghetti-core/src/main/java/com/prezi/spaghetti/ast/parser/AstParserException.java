package com.prezi.spaghetti.ast.parser;

import com.prezi.spaghetti.definition.ModuleDefinitionSource;

public class AstParserException extends RuntimeException {
	public AstParserException(ModuleDefinitionSource source, String message) {
		this(source, message, null);
	}

	public AstParserException(ModuleDefinitionSource source, String message, Throwable cause) {
		super("Parse error in " + source.getLocation() + message, cause);
	}
}
