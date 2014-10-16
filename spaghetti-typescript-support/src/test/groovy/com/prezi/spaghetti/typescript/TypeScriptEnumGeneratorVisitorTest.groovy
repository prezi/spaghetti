package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.internal.parser.ModuleParser
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
		def module = ModuleParser.create(ModuleDefinitionSource.fromString("test", definition)).parse(mockResolver())
		def visitor = new TypeScriptEnumGeneratorVisitor()

		expect:
		visitor.visit(module) == """export class MyEnum {
	/**
	 * Alma.
	 */
	static ALMA:number = 0;
	static BELA:number = 1;
}
"""
	}
}
