package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.generator.InterfaceGeneratorSpecification

class TypeScriptInterfaceGeneratorVisitorTest extends InterfaceGeneratorSpecification {
	def "generate"() {
		def definition = """
interface MyInterface<X> extends Parent<X> {
	/**
	 * Does something.
	 */
	void doSomething(string[] value)

	string[] doSomethingElse(int a, ?int b)
	<T, U> T[] hello(X->(void->int)->U f)
}
"""
		def result = parseAndVisitInterface(definition, new TypeScriptInterfaceGeneratorVisitor(), mockInterface("Parent", mockTypeParameter()))

		expect:
		result == """export interface MyInterface<X> extends com.example.test.Parent<X> {
	/**
	 * Does something.
	 */
	doSomething(value:Array<string>):void;
	doSomethingElse(a:number, b?:number):Array<string>;
	hello<T, U>(f:(arg0: X, arg1: () => number) => U):Array<T>;

}
"""
	}
}
