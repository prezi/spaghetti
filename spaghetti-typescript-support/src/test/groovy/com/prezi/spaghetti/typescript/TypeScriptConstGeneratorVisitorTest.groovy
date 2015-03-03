package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.generator.ConstGeneratorSpecification

class TypeScriptConstGeneratorVisitorTest extends ConstGeneratorSpecification {
	def "generate"() {
		def definition = """
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

		def result = parseAndVisitConst(definition, new TypeScriptConstGeneratorVisitor())

		expect:
		result == """/**
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
