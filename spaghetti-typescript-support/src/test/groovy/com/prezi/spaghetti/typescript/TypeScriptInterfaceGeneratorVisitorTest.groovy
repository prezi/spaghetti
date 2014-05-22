package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.definition.DefinitionParserHelper
import spock.lang.Specification

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptInterfaceGeneratorVisitorTest extends Specification {
	def "generate"() {
		def module = new DefinitionParserHelper().parse("""module com.example.test

interface MyInterface<X> {
	/**
	 * Does something.
	 */
	void doSomething()

	string[] doSomethingElse(int a, int b)
	<T, U> T[] hello(X x, U y)
}
""")
		def visitor = new TypeScriptInterfaceGeneratorVisitor(module)

		expect:
		visitor.processModule() == """export interface MyInterface<X> {

	/**
	 * Does something.
	 */
	doSomething():void;
	doSomethingElse(a:number, b:number):Array<string>;
	hello<T, U>(x:X, y:U):Array<T>;

}
"""
	}
}
