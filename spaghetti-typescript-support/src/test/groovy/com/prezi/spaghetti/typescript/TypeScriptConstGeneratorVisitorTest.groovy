package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.internal.parser.ModuleParser

class TypeScriptConstGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """module com.example.test

/**
 * My dear constants.
 */
const MyConstants {
	int alma = 1
	/**
	 * Bela is -123.
	 */
	int bela = -123
	geza = -1.23
	tibor = "tibor"
}
"""
		def locator = mockLocator(definition)
		def module = ModuleParser.create(locator.source).parse(mockResolver())
		def visitor = new TypeScriptConstGeneratorVisitor()

		expect:
		visitor.visit(module) == """/**
 * My dear constants.
 */
export class MyConstants {
	static alma: number = 1;
	/**
	 * Bela is -123.
	 */
	static bela: number = -123;
	static geza: number = -1.23;
	static tibor: string = "tibor";

}
"""
	}
}
