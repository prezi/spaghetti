package com.prezi.spaghetti.definition

import com.prezi.spaghetti.grammar.ModuleLexer
import com.prezi.spaghetti.grammar.ModuleParser

/**
 * Created by lptr on 22/05/14.
 */
class ModuleParserContext {
	ModuleLexer lexer
	ModuleParser parser
	ParserErrorListener listener
}
