package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.generator.InterfaceGeneratorSpecification

class KotlinInterfaceGeneratorVisitorTest extends InterfaceGeneratorSpecification {
	def "generate"() {
		def definition = """
interface MyInterface<X> extends Tibor<X> {
	/**
	 * Does something.
	 */
	void doSomething()

	@nullable string[] doSomethingElse(@nullable int a, ?int b)
	<T, U> T[] hello(X->(void->int)->U f)
}
"""
		def result = parseAndVisitInterface(definition, new KotlinInterfaceGeneratorVisitor(), mockInterface("Tibor", mockTypeParameter()))

		expect:
		result == """trait MyInterface<X>: com.example.test.Tibor<X> {
	/**
	 * Does something.
	 */
	native fun doSomething():Unit
	native fun doSomethingElse(a:Int?, b:Int? = null):Array<String>?
	native fun <T, U> hello(f:(X,()->Int)->U):Array<T>

}
"""
	}
}
