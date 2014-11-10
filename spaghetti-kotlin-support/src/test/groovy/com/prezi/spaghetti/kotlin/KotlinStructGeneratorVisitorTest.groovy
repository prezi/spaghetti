package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.internal.parser.StructParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import com.prezi.spaghetti.definition.internal.ModuleDefinitionParser

class KotlinStructGeneratorVisitorTest extends AstTestBase {
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
	?string b
	@nullable string c
	@nullable ?string d
	string fun
	@mutable T t
	T convert(T value)
}
"""
		def context = ModuleDefinitionParser.createParser(ModuleDefinitionSource.fromString("test", definition)).parser.structDefinition()
		def parser = new StructParser(context, "com.example.test")
		parser.parse(mockResolver())
		def visitor = new KotlinStructGeneratorVisitor()

		expect:
		visitor.visit(parser.node) == """/**
 * Hey this is my struct!
 */
trait MyStruct<T> {
	val a: Int
	/**
	 * This is field b.
	 */
	[deprecated("struct")]
	val b: String? = null
	val c: String?
	val d: String? = null
	val `fun`: String
	var t: T
	fun convert(value:T):T

}
"""
	}
}
