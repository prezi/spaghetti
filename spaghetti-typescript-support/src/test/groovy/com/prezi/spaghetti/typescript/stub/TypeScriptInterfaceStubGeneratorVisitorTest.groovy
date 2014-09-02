package com.prezi.spaghetti.typescript.stub

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.TypeParameterNode
import com.prezi.spaghetti.ast.internal.DefaultNamedNodeSet
import com.prezi.spaghetti.ast.parser.InterfaceParser
import com.prezi.spaghetti.definition.ModuleDefinitionParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class TypeScriptInterfaceStubGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """interface MyInterface<X> extends Tibor<X> {
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
		def context = ModuleDefinitionParser.createParser(new ModuleDefinitionSource("test", definition)).parser.interfaceDefinition()
		def parser = new InterfaceParser(context, "com.example.test")
		parser.parse(mockResolver([
		        "Tibor": {
					def superIface = Mock(InterfaceNode)
					superIface.qualifiedName >> FQName.fromString("com.example.test.Tibor")
					def mockParam = Mock(TypeParameterNode)
					superIface.typeParameters >> new DefaultNamedNodeSet<TypeParameterNode>("type params", Collections.singleton(mockParam))
					return superIface
				}
		]))
		def iface = parser.node
		def visitor = new TypeScriptInterfaceStubGeneratorVisitor()

		expect:
		visitor.visit(iface) == """export class MyInterfaceStub<X> extends com.example.test.Tibor<X> {
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

}
"""
	}
}
