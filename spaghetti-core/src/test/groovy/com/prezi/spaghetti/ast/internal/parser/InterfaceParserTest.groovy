package com.prezi.spaghetti.ast.internal.parser

import com.google.common.collect.Iterables
import com.prezi.spaghetti.ast.AstTestBase

import static com.prezi.spaghetti.ast.PrimitiveTypeReference.INT

class InterfaceParserTest extends AstTestBase {
	def "parse"() {
		def locator = mockLocator("""
interface MyInterface {
	int add(int a, @nullable int b)
}
""")
		def context = AstTestUtils.parser(locator).interfaceDefinition()
		def resolver = Mock(TypeResolver)
		def parser = new InterfaceParser(locator, context, "com.example.test")

		when:
		parser.parse(resolver)
		def node = parser.node

		then:
		node.name == "MyInterface"
		node.methods*.name == ["add"]
		node.methods*.parameters*.name == [
				["a", "b"],
		]
		node.methods*.returnType == [
				INT,
		]
		node.methods*.parameters*.type == [
				[INT, INT],
		]
		Iterables.get(node.methods.iterator().next().parameters, 1).annotations*.name == ["nullable"]
		0 * _
	}
}
