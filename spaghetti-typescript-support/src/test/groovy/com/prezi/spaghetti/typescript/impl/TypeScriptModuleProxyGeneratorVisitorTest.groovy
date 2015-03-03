package com.prezi.spaghetti.typescript.impl

import com.prezi.spaghetti.generator.ModuleGeneratorSpecification

class TypeScriptModuleProxyGeneratorVisitorTest extends ModuleGeneratorSpecification {
	def "generate"() {
		def definition = """
module com.example.test

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
		def result = parseAndVisitModule(definition, new TypeScriptModuleProxyGeneratorVisitor())

		expect:
		result == """export class __TestModuleProxy {
	doSomething():void {
		com.example.test.TestModule.doSomething();
	}
	doSomethingElse(a:number, b:number):Array<string> {
		return com.example.test.TestModule.doSomethingElse(a, b);
	}
	doSomethingStatic(x:number):number {
		return com.example.test.TestModule.doSomethingStatic(x);
	}
	doSomethingVoid(x:number):void {
		com.example.test.TestModule.doSomethingVoid(x);
	}
	hello<T, U>(t:T, y:U):Array<T> {
		return com.example.test.TestModule.hello(t, y);
	}
	returnT<T>(t:T):com.example.test.MyInterface<T> {
		return com.example.test.TestModule.returnT(t);
	}

}
"""
	}
}
