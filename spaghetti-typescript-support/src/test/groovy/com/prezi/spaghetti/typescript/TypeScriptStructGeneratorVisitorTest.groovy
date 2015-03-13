package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.generator.StructGeneratorSpecification

class TypeScriptStructGeneratorVisitorTest extends StructGeneratorSpecification {
	def "generate"() {
		def definition = """/**
 * Hey this is my struct!
 */
struct MyStruct<T> extends Parent<T> {
	int a
	/**
	 * This is field b.
	 */
	@deprecated("struct")
	?string b
	T t
	T convert(T value)
	Parent<T> parent()
}
"""
		def result = parseAndVisitStruct(definition, new TypeScriptStructGeneratorVisitor(), mockStruct("Parent", mockTypeParameter()))

		expect:
		result == """/**
 * Hey this is my struct!
 */
export interface MyStruct<T> extends com.example.test.Parent<T> {
	a: number;
	/**
	 * This is field b.
	 */
	b?: string;
	t: T;
	convert(value:T):T;
	parent():com.example.test.Parent<T>;

}
"""
	}
}
