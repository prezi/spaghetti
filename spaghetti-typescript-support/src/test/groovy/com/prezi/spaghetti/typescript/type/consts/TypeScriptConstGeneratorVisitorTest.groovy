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
		def result = parseAndVisitConst(definition, new TypeScriptConstGeneratorVisitor(getNamespace()))

		expect:
		result == expectedWith("1", "-123", "-1.23", "\"tibor\"")
	}

	def "generate proxied definition"() {
		def result = parseAndVisitConst(definition, new TypeScriptDependentConstGeneratorVisitor(getNamespace()))

		expect:
		result == expectedWithoutValues()
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

	private static String expectedWithoutValues() {
		"""/**
 * My dear constants.
 */
export module MyConstants {
	export const alma: number;
	/**
	 * Bela is -123.
	 */
	export const bela: number;
	export const geza: number;
	export const tibor: string;

}
"""
	}
}
