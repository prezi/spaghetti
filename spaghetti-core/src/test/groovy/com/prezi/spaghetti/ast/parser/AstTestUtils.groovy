package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.QualifiedTypeNode
import com.prezi.spaghetti.definition.ModuleDefinitionParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import com.prezi.spaghetti.grammar.ModuleParser

class AstTestUtils {
	static ModuleParser parser(String data) {
		ModuleDefinitionParser.createParser(new ModuleDefinitionSource("test", data)).parser
	}

	static TypeResolver resolver(QualifiedTypeNode... nodes) {
		return new SimpleNamedTypeResolver(MissingTypeResolver.INSTANCE, Arrays.asList(nodes))
	}
}
