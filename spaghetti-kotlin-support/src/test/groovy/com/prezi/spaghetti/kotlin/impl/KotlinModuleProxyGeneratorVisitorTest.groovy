package com.prezi.spaghetti.kotlin.impl

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.internal.parser.ModuleParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class KotlinModuleProxyGeneratorVisitorTest extends AstSpecification {
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
		def visitor = new KotlinModuleProxyGeneratorVisitor(module)

		expect:
		visitor.visit(module) == """class __TestModuleProxy {
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
