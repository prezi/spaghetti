package com.prezi.spaghetti.ast.internal.parser

import com.prezi.spaghetti.ast.AstSpecification

class EnumParserTest extends AstSpecification {

	def "parse empty node"() {
		given:
		def node = parseEnum("""
enum MyEnum {
}
""")
		expect:
		node.name == "MyEnum"
		node.values*.name.toList() == []
		node.values*.value.toList() == []
		node.values*.location*.toString() == []
	}

	def "parse implicit node"() {
		given:
		def node = parseEnum("""
enum MyEnum {
	alma,
	bela
}
""")
		expect:
		node.name == "MyEnum"
		node.values*.name.toList() == ["alma", "bela"]
		node.values*.value.toList() == [0, 1]
		node.values*.location*.toString() == ["test:3:1", "test:4:1"]
	}

	def "parse explicit node"() {
		given:
		def node = parseEnum("""
enum MyEnum {
	alma = 2,
	bela = 7
}
""")
		expect:
		node.name == "MyEnum"
		node.values*.name.toList() == ["alma", "bela"]
		node.values*.value.toList() == [2, 7]
		node.values*.location*.toString() == ["test:3:1", "test:4:1"]
	}

	def "parse mixed node"() {
		when:
		parseEnum("""
enum MyEnum {
	alma,
	bela = 1
}
""")
		then:
		def ex = thrown AstParserException
		ex.message == "Parse error in testMixed implicit and explicit entries in enum MyEnum"
	}

	def "parse duplicate values"() {
		when:
		parseEnum("""
enum MyEnum {
	alma = 1,
	bela = 1
}
""")
		then:
		def ex = thrown AstParserException
		ex.message == "Parse error in testDuplicate value in enum MyEnum"
	}


	def "parse duplicate names"() {
		when:
		parseEnum("""
enum MyEnum {
	alma = 1,
	alma = 2
}
""")
		then:
		def ex = thrown InternalAstParserException
		ex.message == " at line 4:1: A(n) enum value with the same name already exists: alma"
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
