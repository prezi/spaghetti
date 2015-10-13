package com.prezi.spaghetti.definition.internal;

import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import com.prezi.spaghetti.internal.grammar.ModuleLexer;
import com.prezi.spaghetti.internal.grammar.ModuleParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class ModuleDefinitionParser {
	public static ModuleParser.ModuleDefinitionContext parse(final ModuleDefinitionSource source) {
		ModuleParserContext parserContext = createParser(source, false);
		ModuleParser.ModuleDefinitionContext tree = parserContext.getParser().moduleDefinition();
		if (parserContext.getListener().isInError()) {
			throw new IllegalArgumentException("Could not parse module definition \'" + source.getLocation() + "\', see errors reported above");
		}
		return tree;
	}

	public static ModuleParser.ModuleDefinitionLegacyContext parseLegacy(final ModuleDefinitionSource source) {
		ModuleParserContext parserContext = createParser(source, true);
		ModuleParser.ModuleDefinitionLegacyContext tree = parserContext.getParser().moduleDefinitionLegacy();
		if (parserContext.getListener().isInError()) {
			throw new IllegalArgumentException("Could not parse module definition \'" + source.getLocation() + "\', see errors reported above");
		}
		return tree;
	}

	public static ModuleParserContext createParser(ModuleDefinitionSource source, boolean silent) {
		ANTLRInputStream input = new ANTLRInputStream(source.getContents());

		ParserErrorListener errorListener = new ParserErrorListener(source.getLocation(), silent);
		ModuleLexer lexer = new ModuleLexer(input);
		lexer.removeErrorListeners();
		lexer.addErrorListener(errorListener);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		ModuleParser parser = new ModuleParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);
		return new ModuleParserContext(lexer, parser, errorListener);
	}

	public static ModuleParserContext createParser(ModuleDefinitionSource source) {
		ANTLRInputStream input = new ANTLRInputStream(source.getContents());

		ParserErrorListener errorListener = new ParserErrorListener(source.getLocation(), false);
		ModuleLexer lexer = new ModuleLexer(input);
		lexer.removeErrorListeners();
		lexer.addErrorListener(errorListener);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		ModuleParser parser = new ModuleParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);
		return new ModuleParserContext(lexer, parser, errorListener);
	}
}
