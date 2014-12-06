package com.prezi.spaghetti.typescript.stub

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.internal.parser.AstTestUtils
import com.prezi.spaghetti.ast.internal.parser.InterfaceParser

class TypeScriptInterfaceStubGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definitionTibor = """interface Tibor<T> {
	T getSomeT()
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
		def tibor = new InterfaceParser(locatorTibor, AstTestUtils.parser(definitionTibor).interfaceDefinition(), "com.example.test")
		tibor.parse(AstTestUtils.resolver())
		def parser = new InterfaceParser(locator, AstTestUtils.parser(definition).interfaceDefinition(), "com.example.test")
		parser.parse(AstTestUtils.resolver(tibor.node))
		def iface = parser.node
		def visitor = new TypeScriptInterfaceStubGeneratorVisitor()

		expect:
		visitor.visit(iface) == """export class MyInterfaceStub<X> implements MyInterface<X> {
	/**
	 * Does something.
	 */
	doSomething():void {}
	boolValue():boolean {
		return false;
	}
	intValue():number {
		return 0;
	}
	floatValue():number {
		return 0;
	}
	stringValue():string {
		return null;
	}
	anyValue():any {
		return null;
	}
	doSomethingElse(a:number, b?:number):Array<string> {
		return null;
	}
	hello<T, U>(f:(arg0: X, arg1: () => number) => U):Array<T> {
		return null;
	}
	getSomeT():X {
		return null;
	}

}
"""
	}
}
