package com.prezi.spaghetti.haxe.type.consts

import com.prezi.spaghetti.generator.ConstGeneratorSpecification
import com.prezi.spaghetti.haxe.type.consts.HaxeConstGeneratorVisitor

class HaxeConstGeneratorVisitorTest extends ConstGeneratorSpecification {
	def definition = """
/**
 * My dear constants.
 */
@deprecated
const MyConstants {
	alma: int = 1;
	/**
	 * Bela is -123.
	 */
	@deprecated("lajos")
	bela: int = -123;
	geza = -1.23;
	tibor = "tibor";
}
"""

	def "generate local definition"() {

		def result = parseAndVisitConst(definition, new HaxeConstGeneratorVisitor())

		expect:
		result == expectedWith("1", "-123", "-1.23", "\"tibor\"")
	}

	def "generate proxied definition"() {
		def result = parseAndVisitConst(definition, new HaxeDependentConstGeneratorVisitor("test"))

		expect:
		result == expectedWith("1", "-123", "-1.23", "\"tibor\"")
		// TODO [knuton] Enable after migratory period
		// result == expectedWith(
		//		"untyped __js__('Spaghetti[\"dependencies\"][\"test\"][\"module\"][\"MyConstants\"][\"alma\"]')",
		//		"untyped __js__('Spaghetti[\"dependencies\"][\"test\"][\"module\"][\"MyConstants\"][\"bela\"]')",
		//		"untyped __js__('Spaghetti[\"dependencies\"][\"test\"][\"module\"][\"MyConstants\"][\"geza\"]')",
		//		"untyped __js__('Spaghetti[\"dependencies\"][\"test\"][\"module\"][\"MyConstants\"][\"tibor\"]')")
	}

	private static String expectedWith(String first, String second, String third, String fourth) {
		"""/**
 * My dear constants.
 */
@:deprecated
@:final class MyConstants {
	public static inline var alma:Int = ${first};
	/**
	 * Bela is -123.
	 */
	@:deprecated("lajos")
	public static inline var bela:Int = ${second};
	public static inline var geza:Float = ${third};
	public static inline var tibor:String = ${fourth};

}
"""
	}
}
