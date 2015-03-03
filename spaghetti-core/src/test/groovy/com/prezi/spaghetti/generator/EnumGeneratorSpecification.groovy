package com.prezi.spaghetti.generator

import com.prezi.spaghetti.ast.ModuleVisitorBase
import com.prezi.spaghetti.ast.ReferableTypeNode
import com.prezi.spaghetti.ast.internal.parser.EnumParser
import com.prezi.spaghetti.ast.internal.parser.Locator
import com.prezi.spaghetti.internal.grammar.ModuleParser

class EnumGeneratorSpecification extends AbstractGeneratorSpecification {
	protected <T> T parseAndVisitEnum(String fragment, ModuleVisitorBase<T> visitor, ReferableTypeNode... existingTypes) {
		return parseAndVisitNode(fragment, visitor, existingTypes) { Locator locator, ModuleParser moduleParser ->
			return new EnumParser(locator, moduleParser.enumDefinition(), "com.example.test")
		}
	}
}
