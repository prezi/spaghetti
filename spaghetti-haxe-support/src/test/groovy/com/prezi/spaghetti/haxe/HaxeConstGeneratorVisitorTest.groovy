package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.definition.DefinitionParserHelper
import spock.lang.Specification

/**
 * Created by lptr on 21/05/14.
 */
class HaxeConstGeneratorVisitorTest extends Specification {
	def "generate"() {
		def module = new DefinitionParserHelper().parse("""module com.example.test

/**
 * My dear constants.
 */
@deprecated
const MyConstants {
	int alma = 1
	/**
	 * Bela is -123.
	 */
	@deprecated("lajos")
	int bela = -123
	geza = -1.23
	tibor = "tibor"
}
""")
		def visitor = new HaxeConstGeneratorVisitor(module)

		expect:
		visitor.processModule() == """
/**
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
