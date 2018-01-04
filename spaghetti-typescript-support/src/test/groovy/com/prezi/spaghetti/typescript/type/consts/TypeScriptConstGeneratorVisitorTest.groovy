package com.prezi.spaghetti.typescript.type.consts

import com.prezi.spaghetti.bundle.ModuleFormat
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

	def "generate proxied definition for UMD format"() {
		def result = parseAndVisitConst(definition, new TypeScriptDependentConstGeneratorVisitor("test", ModuleFormat.UMD))

		expect:
		result == expectedWith("1", "-123", "-1.23", "\"tibor\"")
		// TODO [knuton] Enable after migratory period
		// result == expectedWith(
		//		"Spaghetti[\"dependencies\"][\"test\"][\"MyConstants\"][\"alma\"]",
		//		"Spaghetti[\"dependencies\"][\"test\"][\"MyConstants\"][\"bela\"]",
		//		"Spaghetti[\"dependencies\"][\"test\"][\"MyConstants\"][\"geza\"]",
		//		"Spaghetti[\"dependencies\"][\"test\"][\"MyConstants\"][\"tibor\"]")

	}

	def "generate proxied definition for wrapperless format"() {
		def result = parseAndVisitConst(definition, new TypeScriptDependentConstGeneratorVisitor("test", ModuleFormat.Wrapperless))

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
export module MyConstants {
	export const alma: number = ${first};
	/**
	 * Bela is -123.
	 */
	export const bela: number = ${second};
	export const geza: number = ${third};
	export const tibor: string = ${fourth};

}
"""
	}
}
