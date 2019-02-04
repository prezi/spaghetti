package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.generator.StructGeneratorSpecification

class TypeScriptStructGeneratorVisitorTest extends StructGeneratorSpecification {
	def "generate"() {
		def definition = """/**
 * Hey this is my struct!
 */
struct MyStruct<T> extends Parent<T> {
	a: int;
	/**
	 * This is field b.
	 */
	@deprecated("struct")
	b?: string;
	t: T;
	convert(value: T): T;
	parent(): Parent<T>;
}
"""
		def result = parseAndVisitStruct(definition, new TypeScriptStructGeneratorVisitor(getNamespace()), mockStruct("Parent", mockTypeParameter()))

		expect:
		result == """/**
 * Hey this is my struct!
 */
export interface MyStruct<T> extends Parent<T> {
	a: number;
	/**
	 * This is field b.
	 */
	b?: string;
	t: T;
	convert(value:T):T;
	parent():Parent<T>;

}
"""
	}
}
