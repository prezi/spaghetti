package com.prezi.spaghetti.ast.parser

import spock.lang.Specification

import static com.prezi.spaghetti.ast.PrimitiveType.BOOL
import static com.prezi.spaghetti.ast.PrimitiveType.FLOAT
import static com.prezi.spaghetti.ast.PrimitiveType.INT
import static com.prezi.spaghetti.ast.PrimitiveType.STRING

/**
 * Created by lptr on 29/05/14.
 */
class ConstParserTest extends Specification {
	def "parse"() {
		def context = AstTestUtils.parser("""
const Values {
	boolValue = false
	bool explicitBoolValue = true
	intValue = -1
	int explicitIntValue = 1
	floatValue = -1.0
	float explicitFloatValue = 1.0
	stringValue = "bela"
	string explicitStringValue = "lajos"
}
""").constDefinition()

		def resolver = Mock(TypeResolver)
		def parser = new ConstParser(context, "com.example.test")

		when:
		parser.parse(resolver)
		def node = parser.node

		then:
		node.name == "Values"
		node.entries*.name == [
				"boolValue", "explicitBoolValue",
				"intValue", "explicitIntValue",
				"floatValue", "explicitFloatValue",
				"stringValue", "explicitStringValue",
		]
		node.entries*.value == [
				false, true,
				-1, 1,
				-1.0, 1.0,
				"bela", "lajos"
		]
		node.entries*.type*.type == [
				BOOL, BOOL,
				INT, INT,
				FLOAT, FLOAT,
				STRING, STRING,
		]
		0 * _
	}
}
