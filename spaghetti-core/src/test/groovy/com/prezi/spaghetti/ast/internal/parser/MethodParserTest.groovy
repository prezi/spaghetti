package com.prezi.spaghetti.ast.internal.parser

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.MethodParameterNode
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.ast.TypeParameterNode
import com.prezi.spaghetti.ast.internal.DefaultFQName
import com.prezi.spaghetti.ast.internal.MethodNodeInternal
import com.prezi.spaghetti.ast.internal.NamedNodeSetInternal
import com.prezi.spaghetti.ast.internal.NodeSets
import com.prezi.spaghetti.ast.internal.PrimitiveTypeReferenceInternal
import com.prezi.spaghetti.ast.internal.VoidTypeReferenceInternal

class MethodParserTest extends AstSpecification {
	def "parse simple"() {
		def locator = mockLocator("add(a: int, b: int): int")
		def context = AstParserSpecification.parser(locator).methodDefinition()
		def resolver = Mock(TypeResolver)
		def params = Mock(NamedNodeSetInternal)
		def method = Mock(MethodNodeInternal)

		when:
		MethodParser.parseMethodDefinition(locator, resolver, context, method)

		then:
		_ * method.typeParameters >> NodeSets.newNamedNodeSet("type parameters")
		_ * method.parameters >> params
		1 * method.setReturnType({ it == PrimitiveTypeReferenceInternal.INT })
		1 * params.add({ it instanceof MethodParameterNode && it.name == "a" && it.type == PrimitiveTypeReferenceInternal.INT }, _)
		1 * params.add({ it instanceof MethodParameterNode && it.name == "b" && it.type == PrimitiveTypeReferenceInternal.INT }, _)
		0 * _
	}

	def "parse struct and interface"() {
		def locator = mockLocator("method(i: MyInterface): StructA")
		def context = AstParserSpecification.parser(locator).methodDefinition()
		def mockStructA = Mock(StructNode)
		def mockIfaceI = Mock(InterfaceNode)
		def resolver = Mock(TypeResolver)
		def method = Mock(MethodNodeInternal)
		def params = Mock(NamedNodeSetInternal)

		when:
		MethodParser.parseMethodDefinition(locator, resolver, context, method)

		then:
		_ * resolver.resolveType(_) >> { TypeResolutionContext ctx ->
			if (ctx.name.fullyQualifiedName == "StructA") {
				return mockStructA
			}
			if (ctx.name.fullyQualifiedName == "MyInterface") {
				return mockIfaceI
			}
			throw new UnsupportedOperationException()
		}
		_ * method.typeParameters >> NodeSets.newNamedNodeSet("type parameters")
		_ * mockStructA.typeParameters >> NodeSets.newNamedNodeSet("type parameters")
		_ * method.parameters >> params
		_ * mockIfaceI.typeParameters >> NodeSets.newNamedNodeSet("type parameters")
		1 * method.setReturnType({ it.type == mockStructA })
		1 * params.add({ it instanceof MethodParameterNode && it.name == "i" && it.type.type == mockIfaceI }, _)
		0 * _
	}

	def "parse generic"() {
		def locator = mockLocator("method(t: T): T")
		def context = AstParserSpecification.parser(locator).methodDefinition()
		def typeParam = Mock(TypeParameterNode)
		def resolver = Mock(TypeResolver)
		def method = Mock(MethodNodeInternal)
		def params = Mock(NamedNodeSetInternal)

		when:
		MethodParser.parseMethodDefinition(locator, resolver, context, method)

		then:
		_ * method.typeParameters >> NodeSets.newNamedNodeSet("type parameter", [ typeParam ].toSet())
		_ * typeParam.qualifiedName >> DefaultFQName.fromString("T")
		_ * method.parameters >> params
		1 * method.setReturnType({ it.type == typeParam })
		1 * params.add({ it instanceof MethodParameterNode && it.name == "t" && it.type.type == typeParam }, _)
		0 * _
	}

	def "parse optional"() {
		def locator = mockLocator("method(a: string, b?: int, c?: string): void")
		def context = AstParserSpecification.parser(locator).methodDefinition()
		def resolver = Mock(TypeResolver)
		def method = Mock(MethodNodeInternal)
		def params = Mock(NamedNodeSetInternal)

		when:
		MethodParser.parseMethodDefinition(locator, resolver, context, method)

		then:
		_ * method.typeParameters >> NodeSets.newNamedNodeSet("type parameters")
		_ * method.parameters >> params
		1 * method.setReturnType({ it == VoidTypeReferenceInternal.VOID })
		1 * params.add({
			it instanceof MethodParameterNode &&
					it.name == "a" &&
					it.type == PrimitiveTypeReferenceInternal.STRING &&
					it.optional == false}, _)
		1 * params.add({
			it instanceof MethodParameterNode &&
					it.name == "b" &&
					it.type == PrimitiveTypeReferenceInternal.INT &&
					it.optional == true}, _)
		1 * params.add({
			it instanceof MethodParameterNode &&
					it.name == "c" &&
					it.type == PrimitiveTypeReferenceInternal.STRING &&
					it.optional == true}, _)
		0 * _
	}

	def "parse wrong optional"() {
		def locator = mockLocator("method(a?: int, b: int, c?: int): void")
		def context = AstParserSpecification.parser(locator).methodDefinition()
		def typeParam = Mock(TypeParameterNode)
		def resolver = Mock(TypeResolver)
		def method = Mock(MethodNodeInternal)
		def params = Mock(NamedNodeSetInternal)

		when:
		MethodParser.parseMethodDefinition(locator, resolver, context, method)

		then:
		_ * method.typeParameters >> NodeSets.newNamedNodeSet("type parameters")
		_ * method.parameters >> params
		def ex = thrown InternalAstParserException
		ex.message == " at line 1:16: Only the last parameters of a method can be optional"
	}
}
