package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.ast.TypeParameterReference
import spock.lang.Specification

import static com.prezi.spaghetti.ast.PrimitiveType.ANY
import static com.prezi.spaghetti.ast.PrimitiveType.BOOL
import static com.prezi.spaghetti.ast.PrimitiveType.FLOAT
import static com.prezi.spaghetti.ast.PrimitiveType.INT
import static com.prezi.spaghetti.ast.PrimitiveType.STRING

class StructParserTest extends Specification {
	def "parse primitives"() {
		def context = AstTestUtils.parser("""
struct MyStruct<T> {
	bool boolValue
	int intValue
	float floatValue
	?string stringValue
	any anyValue
	T genericValue
}
""").structDefinition()
		def parser = new StructParser(context, "com.example.test")

		when:
		parser.parse(Mock(TypeResolver))
		def node = parser.node

		then:
		node.name == "MyStruct"
		node.properties*.name == [
				"boolValue",
				"intValue",
				"floatValue",
				"stringValue",
				"anyValue",
				"genericValue",
		]
		node.properties*.optional == [
				false,
				false,
				false,
				true,
				false,
				false,
		]
		(node.properties*.type)[0..4].type == [
				BOOL,
				INT,
				FLOAT,
				STRING,
				ANY,
		]
		(node.properties*.type)[5] instanceof TypeParameterReference
		(node.properties*.type)[5].type.name == "T"
		0 * _
	}

	def "parse local reference"() {
		def context = AstTestUtils.parser("""
struct StructB {
	StructA structA
}
""").structDefinition()
		def mockStructA = Mock(StructNode)
		def resolver = Mock(TypeResolver)
		def parser = new StructParser(context, "com.example.test")

		when:
		parser.parse(resolver)
		def node = parser.node

		then:
		1 * resolver.resolveType(_) >> { TypeResolutionContext ctx ->
			if (ctx.name.fullyQualifiedName == "StructA") {
				return mockStructA
			}
			throw new UnsupportedOperationException()
		}
		node.name == "StructB"
		node.properties*.name == [
				"structA",
		]
		node.properties*.type*.type == [
				mockStructA,
		]
		0 * _
	}

	def "parse method"() {
		def context = AstTestUtils.parser("""
struct MyStruct<T> {
	T convertToRelative(T absolute)
}
""").structDefinition()
		def parser = new StructParser(context, "com.example.test")

		when:
		parser.parse(Mock(TypeResolver))
		def node = parser.node

		then:
		node.name == "MyStruct"
		node.methods*.name == [
				"convertToRelative",
		]
		node.methods*.returnType*.type.name == [
				"T",
		]
		0 * _
	}
}
