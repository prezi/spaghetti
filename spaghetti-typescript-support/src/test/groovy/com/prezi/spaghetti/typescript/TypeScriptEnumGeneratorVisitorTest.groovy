package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.internal.parser.ModuleParser

class TypeScriptEnumGeneratorVisitorTest extends AstSpecification {
	def "generate"() {
		def definition = """module com.example.test

enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA
	BELA
}
"""
		def locator = mockLocator(definition)
		def module = ModuleParser.create(locator.source).parse(mockResolver())
		def visitor = new TypeScriptEnumGeneratorVisitor()

		expect:
		visitor.visit(module) == """export enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA,
	BELA
}
"""
	}
}
