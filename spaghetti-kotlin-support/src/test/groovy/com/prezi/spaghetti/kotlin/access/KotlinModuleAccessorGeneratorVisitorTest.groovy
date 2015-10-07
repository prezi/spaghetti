package com.prezi.spaghetti.kotlin.access

import com.prezi.spaghetti.generator.ModuleGeneratorSpecification

class KotlinModuleAccessorGeneratorVisitorTest extends ModuleGeneratorSpecification {
	def "generate"() {
		def definition = """
module com.example.test {

	extern interface JSON

	interface MyInterface<T> {
		/**
		 * This should not influence anything.
		 */
		add(a: int, b: int): int;
	}

	/**
	 * Initializes module.
	 */
	@deprecated("use doSomething() instead")
	initModule(a: int, b?: int): void;
	doSomething(): JSON[];
	@nullable doStatic(@nullable a: int, b: int): int;
	returnT<T>(t: T): MyInterface<T>;
}
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
