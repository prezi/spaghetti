package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.definition.DefinitionParserHelper
import spock.lang.Specification

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptConstGeneratorVisitorTest extends Specification {
	def "generate"() {
		def module = new DefinitionParserHelper().parse("""module com.example.test

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
""")
		def visitor = new TypeScriptConstGeneratorVisitor(module)

		expect:
		visitor.processModule() == """
/**
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
