//package com.prezi.spaghetti.kotlin
//
//import com.prezi.spaghetti.ast.*
//import com.prezi.spaghetti.ast.internal.parser.InterfaceParser
//import com.prezi.spaghetti.definition.ModuleDefinitionSource
//import com.prezi.spaghetti.definition.internal.ModuleDefinitionParser
//
//class KotlinInterfaceGeneratorVisitorTest extends AstTestBase {
//	def "generate"() {
//		def definition = """interface MyInterface<X> extends Tibor<X> {
//	/**
//	 * Does something.
//	 */
//	void doSomething()
//
//	@nullable string[] doSomethingElse(@nullable int a, ?int b)
//	<T, U> T[] hello(X->(void->int)->U f)
//}
//"""
//		def context = ModuleDefinitionParser.createParser(ModuleDefinitionSource.fromString("test", definition)).parser.interfaceDefinition()
//		def parser = new InterfaceParser(context, "com.example.test")
//		parser.parse(mockResolver([
//		        "Tibor": {
//					def superIface = Mock(InterfaceNode)
//					superIface.qualifiedName >> FQName.fromString("com.example.test.Tibor")
//					def mockParam = Mock(TypeParameterNode)
//					superIface.typeParameters >> NodeSets.newNamedNodeSet("type params", Collections.singleton(mockParam))
//					return superIface
//				}
//		]))
//		def iface = parser.node
//		def visitor = new KotlinInterfaceGeneratorVisitor()
//
//		expect:
//		visitor.visit(iface) == """trait MyInterface<X>: com.example.test.Tibor<X> {
//	/**
//	 * Does something.
//	 */
//	fun doSomething():Unit
//	fun doSomethingElse(a:Int?, b:Int? = null):Array<String>?
//	fun <T, U> hello(f:(X,()->Int)->U):Array<T>
//
//}
//"""
//	}
//}
