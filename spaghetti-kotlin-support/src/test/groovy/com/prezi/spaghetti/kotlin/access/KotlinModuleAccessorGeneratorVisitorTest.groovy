package com.prezi.spaghetti.kotlin.access

import com.prezi.spaghetti.generator.ModuleGeneratorSpecification

class KotlinModuleAccessorGeneratorVisitorTest extends ModuleGeneratorSpecification {
	def "generate"() {
		def definition = """
module com.example.test

extern interface JSON

interface MyInterface<T> {
	/**
	 * This should not influence anything.
	 */
	int add(int a, int b)
}

/**
 * Initializes module.
 */
@deprecated("use doSomething() instead")
void initModule(int a, ?int b)
JSON[] doSomething()
@nullable int doStatic(@nullable int a, int b)
<T> MyInterface<T> returnT(T t)
"""
		def result = parseAndVisitModule(definition, new KotlinModuleAccessorGeneratorVisitor())

		expect:
		result == """native("Spaghetti[\\"dependencies\\"][\\"com.example.test\\"][\\"module\\"]") val moduleRef:TestModule = noImpl

object TestModule {
	val module:TestModule = moduleRef;

	/**
	 * Initializes module.
	 */
	[deprecated("use doSomething() instead")]
	fun initModule(a:Int, b:Int? = null):Unit {
		return module.initModule(a, b)
	}
	fun doSomething():Array<JSON> {
		return module.doSomething()
	}
	fun doStatic(a:Int?, b:Int):Int? {
		return module.doStatic(a, b)
	}
	fun <T> returnT(t:T):com.example.test.MyInterface<T> {
		return module.returnT(t)
	}

}
"""
	}
}
