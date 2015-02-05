package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.NodeSets
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.ast.internal.parser.AstTestUtils
import com.prezi.spaghetti.ast.internal.parser.StructParser

class HaxeStructGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """/**
 * Hey this is my struct!
 */
struct MyStruct<T> extends Parent {
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
		def locator = mockLocator(definition)
		def context = AstTestUtils.parser(locator).structDefinition()
		def parser = new StructParser(locator, context, "com.example.test")
		parser.parse(mockResolver([
				"Parent": {
					Mock(StructNode) {
						getName() >> "Parent"
						getQualifiedName() >> FQName.fromString("com.example.test.Parent")
						getTypeParameters() >> NodeSets.newNamedNodeSet("type parameters")
					}
				}
		]))
		def visitor = new HaxeStructGeneratorVisitor()

		expect:
		visitor.visit(parser.node) == """/**
 * Hey this is my struct!
 */
typedef MyStruct<T> = { > com.example.test.Parent,
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
