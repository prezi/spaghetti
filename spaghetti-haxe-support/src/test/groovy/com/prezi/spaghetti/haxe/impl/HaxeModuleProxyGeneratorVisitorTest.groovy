package com.prezi.spaghetti.haxe.impl

import com.prezi.spaghetti.generator.ModuleGeneratorSpecification

class HaxeModuleProxyGeneratorVisitorTest extends ModuleGeneratorSpecification {
	def "generate"() {
		def definition = """
module com.example.test {

	interface MyInterface<T> {
		/**
		 * This should have nothing to do with the results.
		 */
		someDummyMethod(x: int): void;
	}
	/**
	 * Does something.
	 */
	doSomething(): void;

	doSomethingElse(a: int, b: int): string[];
	/**
	 * No JavaDoc should be generated, this is a non-user-visible class.
	 */
	@deprecated("This should be ignored, too")
	doSomethingStatic(x: int): int;
	doSomethingVoid(x: int): void;
	hello<T, U>(t: T, y: U): T[];
	returnT<T>(t: T): MyInterface<T>;
}
"""
		def result = parseAndVisitModule(definition, new HaxeModuleProxyGeneratorVisitor())

		expect:
		result == """@:final class __TestModuleProxy {
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
