package com.prezi.spaghetti.generator

import com.prezi.spaghetti.ast.ModuleVisitorBase
import com.prezi.spaghetti.ast.ReferableTypeNode
import com.prezi.spaghetti.ast.internal.parser.Locator
import com.prezi.spaghetti.ast.internal.parser.DefaultModuleParser

class ModuleGeneratorSpecification extends AbstractGeneratorSpecification {
	protected <T> T parseAndVisitModule(String fragment, ModuleVisitorBase<T> visitor, ReferableTypeNode... existingTypes) {
		return parseAndVisitNode(fragment, visitor, existingTypes) { Locator locator, com.prezi.spaghetti.internal.grammar.ModuleParser moduleParser ->
			return new DefaultModuleParser(locator, moduleParser.moduleDefinition())
		}
	}
}
