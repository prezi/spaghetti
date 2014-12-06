package com.prezi.spaghetti.haxe.stub

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.internal.parser.AstTestUtils
import com.prezi.spaghetti.ast.internal.parser.InterfaceParser

class HaxeInterfaceStubGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definitionTibor = """interface Tibor<T> {
	T getSomeT()
	com.example.test.MyInterface<T[]> multiply(int max, ?int min)
}
"""
		def locatorTibor = mockLocator(definitionTibor)
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
		def locator = mockLocator(definition)
		def tibor = new InterfaceParser(locatorTibor, AstTestUtils.parser(locatorTibor).interfaceDefinition(), "com.example.test")
		def parser = new InterfaceParser(locator, AstTestUtils.parser(locator).interfaceDefinition(), "com.example.test")
		tibor.parse(AstTestUtils.resolver(tibor.node, parser.node))
		parser.parse(AstTestUtils.resolver(tibor.node, parser.node))
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
	public function multiply(max:Int, ?min:Int):com.example.test.MyInterface<Array<X>> {
		return null;
	}

}
"""
	}
}
