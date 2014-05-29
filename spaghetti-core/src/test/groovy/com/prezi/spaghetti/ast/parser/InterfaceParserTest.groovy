package com.prezi.spaghetti.ast.parser

import spock.lang.Specification

import static com.prezi.spaghetti.ast.PrimitiveTypeReference.INT

/**
 * Created by lptr on 29/05/14.
 */
class InterfaceParserTest extends Specification {
	def "parse"() {
		def context = AstTestUtils.parser("""
interface MyInterface {
	int add(int a, @nullable int b)
}
""").interfaceDefinition()

		def resolver = Mock(TypeResolver)
		def parser = new InterfaceParser(context, "com.example.test")

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
		node.methods.iterator().next().parameters[1].annotations*.name == ["nullable"]
		0 * _
	}
}
