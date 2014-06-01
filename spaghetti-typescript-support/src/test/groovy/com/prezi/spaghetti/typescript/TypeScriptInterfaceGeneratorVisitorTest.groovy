package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.parser.ModuleParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptInterfaceGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """module com.example.test

interface Parent<T> {
}

interface MyInterface<X> extends Parent<X> {
	/**
	 * Does something.
	 */
	void doSomething()

	string[] doSomethingElse(int a, int b)
	<T, U> T[] hello(X->(void->int)->U f)
}
"""
		def parser = ModuleParser.create(new ModuleDefinitionSource("test", definition))
		def module = parser.parse(mockResolver())
		def visitor = new TypeScriptInterfaceGeneratorVisitor()

		expect:
		visitor.visit(module) == """export interface Parent<T> {

}
export interface MyInterface<X> extends com.example.test.Parent<X> {
	/**
	 * Does something.
	 */
	doSomething():void;
	doSomethingElse(a:number, b:number):Array<string>;
	hello<T, U>(f:(arg0: X, arg1: () => number) => U):Array<T>;

}
"""
	}
}
