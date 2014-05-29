package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.TypeParameterNode
import com.prezi.spaghetti.ast.internal.DefaultNamedNodeSet
import com.prezi.spaghetti.ast.parser.InterfaceParser
import com.prezi.spaghetti.definition.ModuleDefinitionParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource

/**
 * Created by lptr on 21/05/14.
 */
class HaxeInterfaceGeneratorVisitorTest extends AstTestBase {
	def "generate"() {
		def definition = """interface MyInterface<X> extends Tibor<X> {
	/**
	 * Does something.
	 */
	void doSomething()

	string[] doSomethingElse(int a, int b)
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
					superIface.typeParameters >> new DefaultNamedNodeSet<TypeParameterNode>("type params", [mockParam].toSet())
					return superIface
				}
		]))
		def iface = parser.node
		def visitor = new HaxeInterfaceGeneratorVisitor()

		expect:
		visitor.visit(iface) == """interface MyInterface<X> extends com.example.test.Tibor<X> {
	/**
	 * Does something.
	 */
	function doSomething():Void;
	function doSomethingElse(a:Int, b:Int):Array<String>;
	function hello<T, U>(f:X->(Void->Int)->U):Array<T>;

}
"""
	}
}
