package com.prezi.spaghetti.definition;

import com.prezi.spaghetti.definition.internal.ParserErrorListener;
import com.prezi.spaghetti.grammar.ModuleLexer;
import com.prezi.spaghetti.grammar.ModuleParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class ModuleDefinitionParser {
	public static ModuleParser.ModuleDefinitionContext parse(final ModuleDefinitionSource source) {
		ModuleParserContext parserContext = createParser(source);
		ModuleParser.ModuleDefinitionContext tree = parserContext.getParser().moduleDefinition();
		if (parserContext.getListener().isInError()) {
			throw new IllegalArgumentException("Could not parse module definition \'" + source.getLocation() + "\', see errors reported above");
		}
		return tree;
	}

	public static ModuleParserContext createParser(ModuleDefinitionSource source) {
		ANTLRInputStream input = new ANTLRInputStream(source.getContents());

		ParserErrorListener errorListener = new ParserErrorListener(source.getLocation());
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
