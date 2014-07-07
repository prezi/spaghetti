package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.parser.ModuleParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class TypeScriptEnumGeneratorVisitorTest extends AstTestBase {
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
		def module = ModuleParser.create(new ModuleDefinitionSource("test", definition)).parse(mockResolver())
		def visitor = new TypeScriptEnumGeneratorVisitor()

		expect:
		visitor.visit(module) == """export enum MyEnum {
	/**
	 * Alma.
	 */
	ALMA = 0,
	BELA = 1
}
"""
	}
}
