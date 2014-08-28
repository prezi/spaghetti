package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.parser.StructParser
import com.prezi.spaghetti.definition.ModuleDefinitionParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

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
	@nullable ?string b
	@mutable T t
	T convert(T value)
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
	@:optional var b (default, never):Null<String>;
	var t:T;
	function convert(value:T):T;

}
"""
	}
}
