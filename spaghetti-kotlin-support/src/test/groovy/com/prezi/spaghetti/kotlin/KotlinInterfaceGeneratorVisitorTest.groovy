package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.TypeParameterNode
import com.prezi.spaghetti.ast.internal.NodeSets
import com.prezi.spaghetti.ast.internal.parser.AstParserSpecification
import com.prezi.spaghetti.ast.internal.parser.InterfaceParser

class KotlinInterfaceGeneratorVisitorTest extends AstSpecification {
	def "generate"() {
		def definition = """interface MyInterface<X> extends Tibor<X> {
	/**
	 * Does something.
	 */
	void doSomething()

	@nullable string[] doSomethingElse(@nullable int a, ?int b)
	<T, U> T[] hello(X->(void->int)->U f)
}
"""
		def locator = mockLocator(definition)
		def context = AstParserSpecification.parser(locator).interfaceDefinition()
		def parser = new InterfaceParser(locator, context, "com.example.test")
		parser.parse(mockResolver([
		        "Tibor": {
					def superIface = Mock(InterfaceNode)
					superIface.qualifiedName >> FQName.fromString("com.example.test.Tibor")
					def mockParam = Mock(TypeParameterNode)
					superIface.typeParameters >> NodeSets.newNamedNodeSet("type params", Collections.singleton(mockParam))
					return superIface
				}
		]))
		def iface = parser.node
		def visitor = new KotlinInterfaceGeneratorVisitor()

		expect:
		visitor.visit(iface) == """trait MyInterface<X>: com.example.test.Tibor<X> {
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
