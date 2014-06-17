package com.prezi.spaghetti.definition

import com.prezi.spaghetti.grammar.ModuleLexer
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream

class ModuleDefinitionParser {
	public static ModuleParser.ModuleDefinitionContext parse(ModuleDefinitionSource source) {
		def parserContext = createParser(source)
		def tree = parserContext.parser.moduleDefinition()
		if (parserContext.listener.inError) {
			throw new IllegalArgumentException("Could not parse module definition '${source.location}', see errors reported above")
		}
		return tree
	}

	public static ModuleParserContext createParser(ModuleDefinitionSource source) {
		def input = new ANTLRInputStream(source.contents)

		def errorListener = new ParserErrorListener(source.location)
		def lexer = new ModuleLexer(input)
		lexer.removeErrorListeners()
		lexer.addErrorListener(errorListener)
		def tokens = new CommonTokenStream(lexer)

		def parser = new ModuleParser(tokens)
		parser.removeErrorListeners()
		parser.addErrorListener(errorListener)
		return new ModuleParserContext(
			parser: parser,
			lexer: lexer,
			listener: errorListener
		)
	}
}
