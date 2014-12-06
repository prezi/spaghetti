package com.prezi.spaghetti.ast.internal.parser

import com.prezi.spaghetti.ast.AstTestBase

class EnumParserTest extends AstTestBase {
	def "parse"() {
		def locator = mockLocator("""
enum MyEnum {
	alma
	bela
}
""")
		def context = AstTestUtils.parser(locator).enumDefinition()
		def resolver = Mock(TypeResolver)
		def parser = new EnumParser(locator, context, "com.example.test")

		when:
		parser.parse(resolver)
		def node = parser.node

		then:
		node.name == "MyEnum"
		node.values*.name.toList() == [
				"alma", "bela"
		]
		node.values*.location*.toString() == [
				"test:3:1",
				"test:4:1"
		]
		0 * _
	}
}
