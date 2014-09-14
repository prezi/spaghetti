package com.prezi.spaghetti.haxe.impl

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.internal.parser.ModuleParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class HaxeModuleProxyGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """module com.example.test

interface MyInterface<T> {
	/**
	 * This should have nothing to do with the results.
	 */
	void someDummyMethod(int x)
}
/**
 * Does something.
 */
void doSomething()

string[] doSomethingElse(int a, int b)
/**
 * No JavaDoc should be generated, this is a non-user-visible class.
 */
@deprecated("This should be ignored, too")
int doSomethingStatic(int x)
void doSomethingVoid(int x)
<T, U> T[] hello(T t, U y)
<T> MyInterface<T> returnT(T t)
"""
		def module = ModuleParser.create(ModuleDefinitionSource.fromString("test", definition)).parse(mockResolver())
		def visitor = new HaxeModuleProxyGeneratorVisitor(module)

		expect:
		visitor.visit(module) == """@:final class __TestModuleProxy {
	public function new() {}
	public function doSomething():Void {
		com.example.test.TestModule.doSomething();
	}
	public function doSomethingElse(a:Int, b:Int):Array<String> {
		return com.example.test.TestModule.doSomethingElse(a, b);
	}
	public function doSomethingStatic(x:Int):Int {
		return com.example.test.TestModule.doSomethingStatic(x);
	}
	public function doSomethingVoid(x:Int):Void {
		com.example.test.TestModule.doSomethingVoid(x);
	}
	public function hello<T, U>(t:T, y:U):Array<T> {
		return com.example.test.TestModule.hello(t, y);
	}
	public function returnT<T>(t:T):com.example.test.MyInterface<T> {
		return com.example.test.TestModule.returnT(t);
	}

}
"""
	}
}
