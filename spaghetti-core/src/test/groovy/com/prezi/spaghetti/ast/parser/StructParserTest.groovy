package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.StructNode
import spock.lang.Specification

import static com.prezi.spaghetti.ast.PrimitiveType.ANY
import static com.prezi.spaghetti.ast.PrimitiveType.BOOL
import static com.prezi.spaghetti.ast.PrimitiveType.FLOAT
import static com.prezi.spaghetti.ast.PrimitiveType.INT
import static com.prezi.spaghetti.ast.PrimitiveType.STRING

/**
 * Created by lptr on 29/05/14.
 */
class StructParserTest extends Specification {
	def "parse primitives"() {
		def context = AstTestUtils.parser("""
struct MyStruct {
	bool boolValue
	int intValue
	float floatValue
	string stringValue
	any anyValue
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
		]
		node.properties*.type*.type == [
				BOOL,
				INT,
				FLOAT,
				STRING,
				ANY,
		]
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
}
