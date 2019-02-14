package com.prezi.spaghetti.ast.internal.parser

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.ast.TypeParameterReference
import com.prezi.spaghetti.ast.internal.NodeSets
import com.prezi.spaghetti.ast.internal.DefaultFQName
import com.prezi.spaghetti.ast.internal.DefaultStructNode

import static com.prezi.spaghetti.ast.PrimitiveType.ANY
import static com.prezi.spaghetti.ast.PrimitiveType.BOOL
import static com.prezi.spaghetti.ast.PrimitiveType.FLOAT
import static com.prezi.spaghetti.ast.PrimitiveType.INT
import static com.prezi.spaghetti.ast.PrimitiveType.STRING

class StructParserTest extends AstSpecification {
	def "parse primitives"() {
		def locator = mockLocator("""
struct MyStruct<T> {
	boolValue: bool;
	intValue: int;
	floatValue: float;
	stringValue?: string;
	anyValue: any;
	genericValue: T;
}
""")
		def context = AstParserSpecification.parser(locator).structDefinition()
		def parser = new StructParser(locator, context, "com.example.test")

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
		def locator = mockLocator("""
struct StructB {
	structA: StructA;
}
""")
		def context = AstParserSpecification.parser(locator).structDefinition()
		def mockStructA = Mock(StructNode)
		def resolver = Mock(TypeResolver)
		def parser = new StructParser(locator, context, "com.example.test")

		when:
		parser.parse(resolver)
		def node = parser.node

		then:
		_ * mockStructA.typeParameters >> NodeSets.newNamedNodeSet("type parameters")
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
		def locator = mockLocator("""
struct MyStruct<T> {
	convertToRelative(absolute: T): T;
}
""")
		def context = AstParserSpecification.parser(locator).structDefinition()
		def parser = new StructParser(locator, context, "com.example.test")

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

	def "parse super struct"() {
		def locator = mockLocator("""
struct StructC extends StructA, StructB {
}
""")
		def context = AstParserSpecification.parser(locator).structDefinition()
		def mockStructA = new DefaultStructNode(null, new DefaultFQName(null, "StructA"))
		def mockStructB = new DefaultStructNode(null, new DefaultFQName(null, "StructB"))
		def resolver = new SimpleNamedTypeResolver(null, [mockStructA, mockStructB])
		def parser = new StructParser(locator, context, "com.example.test")

		when:
		parser.parse(resolver)
		def node = parser.node

		then:
		node.name == "StructC"
		node.superStructs.collect({ it.type }) == [mockStructA, mockStructB]
	}
}
