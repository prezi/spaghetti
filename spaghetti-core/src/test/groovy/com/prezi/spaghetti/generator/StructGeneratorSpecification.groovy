package com.prezi.spaghetti.generator

import com.prezi.spaghetti.ast.ModuleVisitorBase
import com.prezi.spaghetti.ast.ReferableTypeNode
import com.prezi.spaghetti.ast.internal.parser.Locator
import com.prezi.spaghetti.ast.internal.parser.StructParser
import com.prezi.spaghetti.internal.grammar.ModuleParser

class StructGeneratorSpecification extends AbstractGeneratorSpecification {
	protected <T> T parseAndVisitStruct(String fragment, ModuleVisitorBase<T> visitor, ReferableTypeNode... existingTypes) {
		return parseAndVisitNode(fragment, visitor, existingTypes) { Locator locator, ModuleParser moduleParser ->
			return new StructParser(locator, moduleParser.structDefinition(), "com.example.test")
		}
	}
}
