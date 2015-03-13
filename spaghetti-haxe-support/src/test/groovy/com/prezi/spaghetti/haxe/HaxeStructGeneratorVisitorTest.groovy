package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.generator.StructGeneratorSpecification

class HaxeStructGeneratorVisitorTest extends StructGeneratorSpecification {
	def "generate"() {
		def definition = """
/**
 * Hey this is my struct!
 */
struct MyStruct<T> extends Parent<T> {
	int a
	/**
	 * This is field b.
	 */
	@deprecated("struct")
	@nullable ?string b
	@mutable T t
	T convert(T value)
	Parent<T> parent();
}
"""
		def result = parseAndVisitStruct(definition, new HaxeStructGeneratorVisitor(), mockStruct("Parent", mockTypeParameter()))

		expect:
		result == """/**
 * Hey this is my struct!
 */
typedef MyStruct<T> = { > com.example.test.Parent<T>,
	var a (default, never):Int;
	/**
	 * This is field b.
	 */
	@:deprecated("struct")
	@:optional var b (default, never):Null<String>;
	var t:T;
	function convert(value:T):T;
	function parent():com.example.test.Parent<T>;

}
"""
	}
}
