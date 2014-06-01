package com.prezi.spaghetti.haxe.impl

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.parser.ModuleParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

/**
 * Created by lptr on 22/05/14.
 */
class HaxeModuleInterfaceGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """module com.example.test

interface MyInterface<T> {
	/**
	 * This should not influence anything.
	 */
	int add(int a, int b)
}

enum MyEnum {}

/**
 * Does something.
 */
void doSomething()

string[] doSomethingElse(int a, int b, MyEnum en)
// This will not be generated, because it's static
static void doSomethingStatic(int x)
<T, U> T[] hello(T t, U y)
<T> MyInterface<T> returnT(T t)
"""
		def module = ModuleParser.create(new ModuleDefinitionSource("test", definition)).parse(mockResolver())
		def visitor = new HaxeModuleInterfaceGeneratorVisitor()

		expect:
		visitor.visit(module) == """interface ITest {
	/**
	 * Does something.
	 */
	function doSomething():Void;
	function doSomethingElse(a:Int, b:Int, en:com.example.test.MyEnum):Array<String>;
	function hello<T, U>(t:T, y:U):Array<T>;
	function returnT<T>(t:T):com.example.test.MyInterface<T>;

}
"""
	}
}
