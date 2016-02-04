package com.prezi.spaghetti.typescript.type.consts

import com.prezi.spaghetti.generator.ConstGeneratorSpecification
import com.prezi.spaghetti.typescript.type.consts.TypeScriptConstGeneratorVisitor
import com.prezi.spaghetti.typescript.type.consts.TypeScriptDependentConstGeneratorVisitor

class TypeScriptConstGeneratorVisitorTest extends ConstGeneratorSpecification {
	def definition = """
/**
 * My dear constants.
 */
const MyConstants {
	alma: int = 1;
	/**
	 * Bela is -123.
	 */
	bela: int = -123;
	geza = -1.23;
	tibor = "tibor";
}
"""

	def "generate local definition"() {
		def result = parseAndVisitConst(definition, new TypeScriptConstGeneratorVisitor())

		expect:
		result == expectedWith("1", "-123", "-1.23", "\"tibor\"")
	}

	def "generate proxied definition"() {
		def result = parseAndVisitConst(definition, new TypeScriptDependentConstGeneratorVisitor("test"))

		expect:
		result == expectedWith("1", "-123", "-1.23", "\"tibor\"")
		// TODO [knuton] Enable after migratory period
		// result == expectedWith(
		//		"Spaghetti[\"dependencies\"][\"test\"][\"module\"][\"MyConstants\"][\"alma\"]",
		//		"Spaghetti[\"dependencies\"][\"test\"][\"module\"][\"MyConstants\"][\"bela\"]",
		//		"Spaghetti[\"dependencies\"][\"test\"][\"module\"][\"MyConstants\"][\"geza\"]",
		//		"Spaghetti[\"dependencies\"][\"test\"][\"module\"][\"MyConstants\"][\"tibor\"]")

	}

	private static String expectedWith(String first, String second, String third, String fourth) {
		"""/**
 * My dear constants.
 */
export class MyConstants {
	static alma: number = ${first};
	/**
	 * Bela is -123.
	 */
	static bela: number = ${second};
	static geza: number = ${third};
	static tibor: string = ${fourth};

}
"""
	}
}
