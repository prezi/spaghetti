package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.definition.DefinitionParserHelper
import spock.lang.Specification

/**
 * Created by lptr on 21/05/14.
 */
class HaxeStructGeneratorVisitorTest extends Specification {
	def "generate"() {
		def module = new DefinitionParserHelper().parse("""module com.example.test

/**
 * Hey this is my struct!
 */
struct MyStruct {
	int a
	/**
	 * This is field b.
	 */
	@deprecated("struct")
	string b
}
""")
		def visitor = new HaxeStructGeneratorVisitor(module)

		expect:
		visitor.processModule() == """
/**
 * Hey this is my struct!
 */
typedef MyStruct = {
	var a (default, never):Int;

	/**
	 * This is field b.
	 */
	@:deprecated("struct")
	var b (default, never):String;

}
"""
	}
}
