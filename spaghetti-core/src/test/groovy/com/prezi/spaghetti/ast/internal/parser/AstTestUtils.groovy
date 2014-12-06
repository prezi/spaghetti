package com.prezi.spaghetti.ast.internal.parser

import com.prezi.spaghetti.ast.QualifiedTypeNode
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import com.prezi.spaghetti.definition.internal.ModuleDefinitionParser
import com.prezi.spaghetti.internal.grammar.ModuleParser

class AstTestUtils {
	static ModuleParser parser(String data) {
		ModuleDefinitionParser.createParser(ModuleDefinitionSource.fromString("test", data)).parser
	}

	static ModuleParser parser(Locator locator) {
		ModuleDefinitionParser.createParser(locator.source).parser
	}

	static TypeResolver resolver(QualifiedTypeNode... nodes) {
		return new SimpleNamedTypeResolver(MissingTypeResolver.INSTANCE, Arrays.asList(nodes))
	}
}
