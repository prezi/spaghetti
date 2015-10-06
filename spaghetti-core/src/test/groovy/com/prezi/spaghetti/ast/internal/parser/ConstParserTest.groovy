package com.prezi.spaghetti.ast.internal.parser

import static com.prezi.spaghetti.ast.PrimitiveType.BOOL
import static com.prezi.spaghetti.ast.PrimitiveType.FLOAT
import static com.prezi.spaghetti.ast.PrimitiveType.INT
import static com.prezi.spaghetti.ast.PrimitiveType.STRING

class ConstParserTest extends AstParserSpecification {
	def "parse"() {
		def locator = mockLocator("""
const Values {
	boolValue = false;
	explicitBoolValue: bool = true;
	intValue = -1;
	explicitIntValue: int = 1;
	floatValue = -1.0;
	explicitFloatValue: float = 1.0;
	stringValue = "bela";
	explicitStringValue: string = "lajos";
}
""")
		def context = parser(locator).constDefinition()
		def resolver = Mock(TypeResolver)
		def parser = new ConstParser(locator, context, "com.example.test")

		when:
		parser.parse(resolver)
		def node = parser.node

		then:
		node.name == "Values"
		node.location.toString() == "test:2:6"
		node.entries*.name == [
				"boolValue", "explicitBoolValue",
				"intValue", "explicitIntValue",
				"floatValue", "explicitFloatValue",
				"stringValue", "explicitStringValue",
		]
		node.entries*.location*.toString() == [
				"test:3:1",
				"test:4:1",
				"test:5:1",
				"test:6:1",
				"test:7:1",
				"test:8:1",
				"test:9:1",
				"test:10:1",
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
