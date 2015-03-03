package com.prezi.spaghetti.generator

import com.prezi.spaghetti.ast.ModuleVisitorBase
import com.prezi.spaghetti.ast.ReferableTypeNode
import com.prezi.spaghetti.ast.internal.parser.ConstParser
import com.prezi.spaghetti.ast.internal.parser.Locator
import com.prezi.spaghetti.internal.grammar.ModuleParser

class ConstGeneratorSpecification extends AbstractGeneratorSpecification {
	protected <T> T parseAndVisitConst(String fragment, ModuleVisitorBase<T> visitor, ReferableTypeNode... existingTypes) {
		return parseAndVisitNode(fragment, visitor, existingTypes) { Locator locator, ModuleParser moduleParser ->
			return new ConstParser(locator, moduleParser.constDefinition(), "com.example.test")
		}
	}
}
