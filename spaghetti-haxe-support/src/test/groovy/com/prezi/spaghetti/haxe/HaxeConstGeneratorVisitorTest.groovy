package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.parser.ModuleParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

/**
 * Created by lptr on 21/05/14.
 */
class HaxeConstGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """module com.example.test

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
"""
		def parser = ModuleParser.create(new ModuleDefinitionSource("test", definition))
		def module = parser.parse(mockResolver())
		def visitor = new HaxeConstGeneratorVisitor()

		expect:
		visitor.visit(module) == """/**
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
