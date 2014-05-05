package com.prezi.spaghetti

import com.prezi.spaghetti.grammar.ModuleLexer
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream

/**
 * Created by lptr on 05/05/14.
 */
class ModuleDefinitionParser {
	public static ModuleParser.ModuleDefinitionContext parse(ModuleDefinitionSource source) {
		def input = new ANTLRInputStream(source.contents)
		def lexer = new ModuleLexer(input)
		def tokens = new CommonTokenStream(lexer)
		def parser = new ModuleParser(tokens)
		parser.removeErrorListeners()
		parser.addErrorListener(new ParserErrorListener(source.location))
		def tree = parser.moduleDefinition()
		if (parser.numberOfSyntaxErrors > 0) {
			throw new IllegalArgumentException("Could not parse module definition '${source.location}', see errors reported above")
		}
		return tree
	}
}
