package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.parser.StructParser
import com.prezi.spaghetti.definition.ModuleDefinitionParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

/**
 * Created by lptr on 21/05/14.
 */
class HaxeStructGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """/**
 * Hey this is my struct!
 */
struct MyStruct<T> {
	int a
	/**
	 * This is field b.
	 */
	@deprecated("struct")
	string b
	@mutable T t
}
"""
		def context = ModuleDefinitionParser.createParser(new ModuleDefinitionSource("test", definition)).parser.structDefinition()
		def parser = new StructParser(context, "com.example.test")
		parser.parse(mockResolver())
		def visitor = new HaxeStructGeneratorVisitor()

		expect:
		visitor.visit(parser.node) == """/**
 * Hey this is my struct!
 */
typedef MyStruct<T> = {
	var a (default, never):Int;
	/**
	 * This is field b.
	 */
	@:deprecated("struct")
	var b (default, never):String;
	var t:T;

}
"""
	}
}
