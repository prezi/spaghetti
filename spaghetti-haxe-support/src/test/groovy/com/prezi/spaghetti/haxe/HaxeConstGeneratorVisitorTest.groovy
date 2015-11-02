package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.generator.ConstGeneratorSpecification

class HaxeConstGeneratorVisitorTest extends ConstGeneratorSpecification {
	def "generate"() {
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

		def result = parseAndVisitConst(definition, new HaxeConstGeneratorVisitor())

		expect:
		result == """/**
 * My dear constants.
 */
@:deprecated
@:final class MyConstants {
	public static inline var alma:Int = 1;
	/**
	 * Bela is -123.
	 */
	@:deprecated("lajos")
	public static inline var bela:Int = -123;
	public static inline var geza:Float = -1.23;
	public static inline var tibor:String = "tibor";

}
"""
	}
}
