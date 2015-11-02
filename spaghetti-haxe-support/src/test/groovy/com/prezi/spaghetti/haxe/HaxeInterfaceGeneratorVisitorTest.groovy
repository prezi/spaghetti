package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.generator.InterfaceGeneratorSpecification

class HaxeInterfaceGeneratorVisitorTest extends InterfaceGeneratorSpecification {
	def "generate"() {
		def definition = """interface MyInterface<X> extends Tibor<X> {
	/**
	 * Does something.
	 */
	doSomething(): void;

	@nullable doSomethingElse(@nullable a: int, b?: int): string[];
	hello<T, U>(f: (X, () -> int) -> U): T[];
}
"""
		def result = parseAndVisitInterface(definition, new HaxeInterfaceGeneratorVisitor(), mockInterface("Tibor", "com.example.test.Tibor", mockTypeParameter()))

		expect:
		result == """interface MyInterface<X> extends com.example.test.Tibor<X> {
	/**
	 * Does something.
	 */
	function doSomething():Void;
	function doSomethingElse(a:Null<Int>, ?b:Int):Null<Array<String>>;
	function hello<T, U>(f:X->(Void->Int)->U):Array<T>;

}
"""
	}
}
