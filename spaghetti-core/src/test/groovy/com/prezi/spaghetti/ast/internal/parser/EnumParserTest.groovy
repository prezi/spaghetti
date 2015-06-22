package com.prezi.spaghetti.ast.internal.parser

import com.prezi.spaghetti.ast.AstSpecification

class EnumParserTest extends AstSpecification {

	def "parse"() {
		when:
		def emptyNode = parseEnum("""
enum MyEnum {
}
""")
		then:
		emptyNode.name == "MyEnum"
		emptyNode.values*.name.toList() == []
		emptyNode.values*.value.toList() == []
		emptyNode.values*.location*.toString() == []
		0 * _

		when:
		def implicitNode = parseEnum("""
enum MyEnum {
	alma
	bela
}
""")
		then:
		implicitNode.name == "MyEnum"
		implicitNode.values*.name.toList() == ["alma", "bela"]
		implicitNode.values*.value.toList() == [0, 1]
		implicitNode.values*.location*.toString() == ["test:3:1", "test:4:1"]
		0 * _

		when:
		def explicitNode = parseEnum("""
enum MyEnum {
	alma = 2
	bela = 7
}
""")
		then:
		explicitNode.name == "MyEnum"
		explicitNode.values*.name.toList() == ["alma", "bela"]
		explicitNode.values*.value.toList() == [2, 7]
		explicitNode.values*.location*.toString() == ["test:3:1", "test:4:1"]
		0 * _

		when:
		parseEnum("""
enum MyEnum {
	alma
	bela = 1
}
""")
		then:
		def mixedEx = thrown AstParserException
		mixedEx.message == "Parse error in testMixed implicit and explicit entries in enum MyEnum"

		when:
		parseEnum("""
enum MyEnum {
	alma = 1
	bela = 1
}
""")
		then:
		def dupeEx = thrown AstParserException
		dupeEx.message == "Parse error in testDuplicate value in enum MyEnum"
	}

	def parseEnum(String enumDef) {
		def locator = mockLocator(enumDef)
		def context = AstParserSpecification.parser(locator).enumDefinition()
		def resolver = Mock(TypeResolver)
		def parser = new EnumParser(locator, context, "com.example.test")

		parser.parse(resolver)
		return parser.node
	}
}
