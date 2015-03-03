package com.prezi.spaghetti.generator

import com.prezi.spaghetti.ast.ModuleVisitorBase
import com.prezi.spaghetti.ast.ReferableTypeNode
import com.prezi.spaghetti.ast.internal.parser.InterfaceParser
import com.prezi.spaghetti.ast.internal.parser.Locator
import com.prezi.spaghetti.internal.grammar.ModuleParser

class InterfaceGeneratorSpecification extends AbstractGeneratorSpecification {
	protected <T> T parseAndVisitInterface(String fragment, ModuleVisitorBase<T> visitor, ReferableTypeNode... existingTypes) {
		return parseAndVisitNode(fragment, visitor, existingTypes) { Locator locator, ModuleParser moduleParser ->
			return new InterfaceParser(locator, moduleParser.interfaceDefinition(), "com.example.test")
		}
	}
}
