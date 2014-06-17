package com.prezi.spaghetti.definition

import com.prezi.spaghetti.grammar.ModuleLexer
import com.prezi.spaghetti.grammar.ModuleParser

class ModuleParserContext {
	ModuleLexer lexer
	ModuleParser parser
	ParserErrorListener listener
}
