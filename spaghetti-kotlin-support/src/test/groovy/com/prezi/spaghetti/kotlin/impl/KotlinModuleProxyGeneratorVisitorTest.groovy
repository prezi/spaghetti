package com.prezi.spaghetti.kotlin.impl

import com.prezi.spaghetti.generator.ModuleGeneratorSpecification

class KotlinModuleProxyGeneratorVisitorTest extends ModuleGeneratorSpecification {
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
		def result = parseAndVisitModule(definition, new KotlinModuleProxyGeneratorVisitor())

		expect:
		result == """class __TestModuleProxy {
	fun doSomething():Unit {
		return com.example.test.TestModule.doSomething()
	}
	fun doSomethingElse(a:Int, b:Int):Array<String> {
		return com.example.test.TestModule.doSomethingElse(a, b)
	}
	fun doSomethingStatic(x:Int):Int {
		return com.example.test.TestModule.doSomethingStatic(x)
	}
	fun doSomethingVoid(x:Int):Unit {
		return com.example.test.TestModule.doSomethingVoid(x)
	}
	fun <T, U> hello(t:T, y:U):Array<T> {
		return com.example.test.TestModule.hello(t, y)
	}
	fun <T> returnT(t:T):com.example.test.MyInterface<T> {
		return com.example.test.TestModule.returnT(t)
	}

}
"""
	}
}
