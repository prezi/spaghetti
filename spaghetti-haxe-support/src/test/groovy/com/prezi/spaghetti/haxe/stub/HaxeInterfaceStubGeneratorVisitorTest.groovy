package com.prezi.spaghetti.haxe.stub

import com.prezi.spaghetti.generator.ModuleGeneratorSpecification

class HaxeInterfaceStubGeneratorVisitorTest extends ModuleGeneratorSpecification {
	def "generate"() {
		def definition = """
module com.example.test

interface Tibor<T> {
	T getSomeT()
	com.example.test.MyInterface<T[]> multiply(int max, ?int min)
}

interface MyInterface<X> extends com.example.test.Tibor<X> {
	/**
	 * Does something.
	 */
	void doSomething()

	bool boolValue()
	int intValue()
	float floatValue()
	string stringValue()
	any anyValue()
	@nullable string[] doSomethingElse(@nullable int a, ?int b)
	<T, U> T[] hello(X->(void->int)->U f)
}
"""
		def result = parseAndVisitModule(definition, new HaxeInterfaceStubGeneratorVisitor())

		expect:
		result == """class TiborStub<T> implements Tibor<T> {
	public function getSomeT():T {
		return null;
	}
	public function multiply(max:Int, ?min:Int):com.example.test.MyInterface<Array<T>> {
		return null;
	}

}
class MyInterfaceStub<X> implements MyInterface<X> {
	/**
	 * Does something.
	 */
	public function doSomething():Void {}
	public function boolValue():Bool {
		return false;
	}
	public function intValue():Int {
		return 0;
	}
	public function floatValue():Float {
		return 0;
	}
	public function stringValue():String {
		return null;
	}
	public function anyValue():Dynamic {
		return null;
	}
	public function doSomethingElse(a:Null<Int>, ?b:Int):Null<Array<String>> {
		return null;
	}
	public function hello<T, U>(f:X->(Void->Int)->U):Array<T> {
		return null;
	}
	public function getSomeT():X {
		return null;
	}
	public function multiply(max:Int, ?min:Int):com.example.test.MyInterface<Array<X>> {
		return null;
	}

}
"""
	}
}
