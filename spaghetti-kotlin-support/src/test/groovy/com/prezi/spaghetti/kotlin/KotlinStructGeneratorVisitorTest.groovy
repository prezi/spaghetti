package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.ast.internal.NodeSets
import com.prezi.spaghetti.ast.internal.parser.AstParserSpecification
import com.prezi.spaghetti.ast.internal.parser.StructParser

class KotlinStructGeneratorVisitorTest extends AstSpecification {
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
	?string b
	@nullable string c
	@nullable ?string d
	@mutable T t
	T convert(T value)
}
"""
		def locator = mockLocator(definition)
		def context = AstParserSpecification.parser(locator).structDefinition()
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
		def visitor = new KotlinStructGeneratorVisitor()

		expect:
		visitor.visit(parser.node) == """/**
 * Hey this is my struct!
 */
trait MyStruct<T> : com.example.test.Parent {
	val a: Int
	/**
	 * This is field b.
	 */
	[deprecated("struct")]
	val b: String? = null
	val c: String?
	val d: String? = null
	var t: T
	fun convert(value:T):T

}
"""
	}
}
