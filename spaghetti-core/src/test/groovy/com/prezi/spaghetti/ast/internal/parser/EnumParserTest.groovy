package com.prezi.spaghetti.ast.internal.parser

import spock.lang.Specification

class EnumParserTest extends Specification {
	def "parse"() {
		def context = AstTestUtils.parser("""
enum MyEnum {
	alma
	bela
}
""").enumDefinition()

		def resolver = Mock(TypeResolver)
		def parser = new EnumParser(context, "com.example.test")

		when:
		parser.parse(resolver)
		def node = parser.node

		then:
		node.name == "MyEnum"
		node.values*.name.toList() == ["alma", "bela"]
		0 * _
	}
}
