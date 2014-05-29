package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.definition.ModuleDefinitionParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import com.prezi.spaghetti.grammar.ModuleParser

/**
 * Created by lptr on 29/05/14.
 */
class AstTestUtils {
	static ModuleParser parser(String data) {
		ModuleDefinitionParser.createParser(new ModuleDefinitionSource(
				location: "test",
				contents: data)).parser
	}
}
