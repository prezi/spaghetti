package com.prezi.spaghetti.kotlin.impl

import com.prezi.spaghetti.generator.ModuleGeneratorSpecification

class KotlinModuleProxyGeneratorVisitorTest extends ModuleGeneratorSpecification {
	def "generate"() {
		def definition = """module com.example.test

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
