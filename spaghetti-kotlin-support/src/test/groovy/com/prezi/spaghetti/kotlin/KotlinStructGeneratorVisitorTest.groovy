package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.generator.StructGeneratorSpecification

class KotlinStructGeneratorVisitorTest extends StructGeneratorSpecification {
	def "generate"() {
		def definition = """
/**
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
		def result = parseAndVisitStruct(definition, new KotlinStructGeneratorVisitor(), mockStruct("Parent"))

		expect:
		result == """/**
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
