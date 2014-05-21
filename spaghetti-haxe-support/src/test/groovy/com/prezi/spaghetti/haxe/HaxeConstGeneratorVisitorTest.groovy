package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.definition.DefinitionParserHelper
import spock.lang.Specification

/**
 * Created by lptr on 21/05/14.
 */
class HaxeConstGeneratorVisitorTest extends Specification {
	def "generate"() {
		def module = new DefinitionParserHelper().parse("""module com.example.test

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
		visitor.processModule() == """@:final class MyConstants {
	public static var alma(default, never):Int = 1;

	/**
	 * Bela is -123.
	 */
	@:deprecated("Deprecated constant \\"MyConstants\\": lajos")
	public static var bela(default, never):Int = -123;
	public static var geza(default, never):Float = -1.23;
	public static var tibor(default, never):String = "tibor";

}
"""
	}
}
