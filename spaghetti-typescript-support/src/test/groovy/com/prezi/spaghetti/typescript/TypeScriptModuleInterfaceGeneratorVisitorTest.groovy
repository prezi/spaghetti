package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.definition.DefinitionParserHelper
import spock.lang.Specification

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptModuleInterfaceGeneratorVisitorTest extends Specification {
	def "generate"() {
		def module = new DefinitionParserHelper().parse("""module com.example.test

interface MyInterface<T> {}

/**
 * Does something.
 */
void doSomething()

string[] doSomethingElse(int a, int b)
<T, U> T[] hello(T t, U y)
<T> MyInterface<T> returnT(T t)
""")
		def visitor = new TypeScriptModuleInterfaceGeneratorVisitor(module, "IInterface")

		expect:
		visitor.processModule() == """export interface IInterface {

	/**
	 * Does something.
	 */
	doSomething():void;
	doSomethingElse(a:number, b:number):Array<string>;
	hello<T, U>(t:T, y:U):Array<T>;
	returnT<T>(t:T):com.example.test.MyInterface<T>;

}
"""
	}
}
