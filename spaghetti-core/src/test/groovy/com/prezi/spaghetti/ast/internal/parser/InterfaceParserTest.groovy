package com.prezi.spaghetti.ast.internal.parser

import com.google.common.collect.Iterables
import com.prezi.spaghetti.ast.AstSpecification

import static com.prezi.spaghetti.ast.internal.PrimitiveTypeReferenceInternal.INT

class InterfaceParserTest extends AstSpecification {
	def "parse"() {
		def locator = mockLocator("""
interface MyInterface {
	int add(int a, @nullable int b)
}
""")
		def context = AstParserSpecification.parser(locator).interfaceDefinition()
		def resolver = Mock(TypeResolver)
		def parser = new InterfaceParser(locator, context, "com.example.test")

		when:
		parser.parse(resolver)
		def node = parser.node

		then:
		node.name == "MyInterface"
		node.methods*.name == ["add"]
		node.methods*.location*.toString() == ["test:3:5"]
		node.methods*.parameters*.name == [
				["a", "b"],
		]
		node.methods[0].parameters[0].location.toString() == "test:3:13"
		node.methods[0].parameters[1].location.toString() == "test:3:30"
		node.methods*.returnType == [
				INT,
		]
		node.methods*.returnType*.location*.toString() == [
		        "test:3:1"
		]
		node.methods*.parameters*.type == [
				[INT, INT],
		]
		Iterables.get(node.methods.iterator().next().parameters, 1).annotations*.name == ["nullable"]
		0 * _
	}
}
