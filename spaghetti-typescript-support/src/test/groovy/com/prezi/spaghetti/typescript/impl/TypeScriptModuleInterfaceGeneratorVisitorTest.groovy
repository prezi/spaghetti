package com.prezi.spaghetti.typescript.impl

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.parser.ModuleParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class TypeScriptModuleInterfaceGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """module com.example.test

interface MyInterface<T> {
	/**
	 * This should have nothing to do with the results.
	 */
	void someDummyMethod(int x)
}
enum MyEnum {}

/**
 * Does something.
 */
void doSomething()

string[] doSomethingElse(int a, int b, ?MyEnum en)
// This will not be generated, because it's static
static void doSomethingStatic(int x)
<T, U> T[] hello(T t, U y)
<T> MyInterface<T> returnT(T t)
"""
		def module = ModuleParser.create(new ModuleDefinitionSource("test", definition)).parse(mockResolver())
		def visitor = new TypeScriptModuleInterfaceGeneratorVisitor()

		expect:
		visitor.visit(module) == """export interface ITest {
	/**
	 * Does something.
	 */
	doSomething():void;
	doSomethingElse(a:number, b:number, en?:com.example.test.MyEnum):Array<string>;
	hello<T, U>(t:T, y:U):Array<T>;
	returnT<T>(t:T):com.example.test.MyInterface<T>;

}
"""
	}
}
