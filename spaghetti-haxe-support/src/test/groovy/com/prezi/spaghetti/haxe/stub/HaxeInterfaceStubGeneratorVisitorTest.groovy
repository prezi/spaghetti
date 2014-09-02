package com.prezi.spaghetti.haxe.stub

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.TypeMethodNode
import com.prezi.spaghetti.ast.TypeParameterNode
import com.prezi.spaghetti.ast.internal.DefaultNamedNodeSet
import com.prezi.spaghetti.ast.parser.InterfaceParser
import com.prezi.spaghetti.definition.ModuleDefinitionParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

class HaxeInterfaceStubGeneratorVisitorTest extends AstTestBase {
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
					superIface.superInterfaces >> [].toSet()
					superIface.methods >> new DefaultNamedNodeSet<TypeMethodNode>("methods")
					def mockParam = Mock(TypeParameterNode)
					superIface.typeParameters >> new DefaultNamedNodeSet<TypeParameterNode>("type params", Collections.singleton(mockParam))
					return superIface
				}
		]))
		def iface = parser.node
		def visitor = new HaxeInterfaceStubGeneratorVisitor()

		expect:
		visitor.visit(iface) == """class MyInterfaceStub<X> implements com.example.test.Tibor<X> {
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

}
"""
	}
}
