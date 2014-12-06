package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.internal.parser.ModuleParser

class TypeScriptInterfaceGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """module com.example.test

interface Parent<T> {
}

enum Fruit {
	ALMA
	BELA
}

interface MyInterface<X> extends Parent<X> {
	/**
	 * Does something.
	 */
	void doSomething(Fruit[] value)

	string[] doSomethingElse(int a, ?int b)
	<T, U> T[] hello(X->(void->int)->U f)
}
"""
		def locator = mockLocator(definition)
		def module = ModuleParser.create(locator.source).parse(mockResolver())
		def visitor = new TypeScriptInterfaceGeneratorVisitor()

		expect:
		visitor.visit(module) == """export interface Parent<T> {

}
export interface MyInterface<X> extends com.example.test.Parent<X> {
	/**
	 * Does something.
	 */
	doSomething(value:Array<com.example.test.Fruit>):void;
	doSomethingElse(a:number, b?:number):Array<string>;
	hello<T, U>(f:(arg0: X, arg1: () => number) => U):Array<T>;

}
"""
	}
}
