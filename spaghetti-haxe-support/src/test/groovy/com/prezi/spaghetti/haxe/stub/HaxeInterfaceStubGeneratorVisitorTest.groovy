package com.prezi.spaghetti.haxe.stub

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.internal.parser.AstTestUtils
import com.prezi.spaghetti.ast.internal.parser.InterfaceParser

class HaxeInterfaceStubGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definitionTibor = """interface Tibor<T> {
	T getSomeT()
}
"""
		def definition = """interface MyInterface<X> extends com.example.test.Tibor<X> {
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
		def tibor = new InterfaceParser(AstTestUtils.parser(definitionTibor).interfaceDefinition(), "com.example.test")
		tibor.parse(AstTestUtils.resolver())
		def parser = new InterfaceParser(AstTestUtils.parser(definition).interfaceDefinition(), "com.example.test")
		parser.parse(AstTestUtils.resolver(tibor.node))
		def iface = parser.node
		def visitor = new HaxeInterfaceStubGeneratorVisitor()

		expect:
		visitor.visit(iface) == """class MyInterfaceStub<X> implements MyInterface<X> {
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

}
"""
	}
}
