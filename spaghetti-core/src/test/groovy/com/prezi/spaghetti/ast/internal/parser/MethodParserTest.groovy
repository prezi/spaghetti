package com.prezi.spaghetti.ast.internal.parser

import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.MethodParameterNode
import com.prezi.spaghetti.ast.PrimitiveTypeReference
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.ast.TypeParameterNode
import com.prezi.spaghetti.ast.VoidTypeReference
import com.prezi.spaghetti.ast.internal.MethodNodeInternal
import com.prezi.spaghetti.ast.internal.NamedNodeSetInternal
import com.prezi.spaghetti.ast.internal.NodeSets

class MethodParserTest extends AstTestBase {
	def "parse simple"() {
		def locator = mockLocator("int add(int a, int b)")
		def context = AstTestUtils.parser(locator).methodDefinition()
		def resolver = Mock(TypeResolver)
		def params = Mock(NamedNodeSetInternal)
		def method = Mock(MethodNodeInternal)

		when:
		MethodParser.parseMethodDefinition(locator, resolver, context, method)

		then:
		_ * method.typeParameters >> NodeSets.newNamedNodeSet("type parameters")
		_ * method.parameters >> params
		1 * method.setReturnType({ it == PrimitiveTypeReference.INT })
		1 * params.add({ it instanceof MethodParameterNode && it.name == "a" && it.type == PrimitiveTypeReference.INT }, _)
		1 * params.add({ it instanceof MethodParameterNode && it.name == "b" && it.type == PrimitiveTypeReference.INT }, _)
		0 * _
	}

	def "parse struct and interface"() {
		def locator = mockLocator("StructA method(MyInterface i)")
		def context = AstTestUtils.parser(locator).methodDefinition()
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
		def locator = mockLocator("T method(T t)")
		def context = AstTestUtils.parser(locator).methodDefinition()
		def typeParam = Mock(TypeParameterNode)
		def resolver = Mock(TypeResolver)
		def method = Mock(MethodNodeInternal)
		def params = Mock(NamedNodeSetInternal)

		when:
		MethodParser.parseMethodDefinition(locator, resolver, context, method)

		then:
		_ * method.typeParameters >> NodeSets.newNamedNodeSet("type parameter", [ typeParam ].toSet())
		_ * typeParam.qualifiedName >> FQName.fromString("T")
		_ * method.parameters >> params
		1 * method.setReturnType({ it.type == typeParam })
		1 * params.add({ it instanceof MethodParameterNode && it.name == "t" && it.type.type == typeParam }, _)
		0 * _
	}

	def "parse optional"() {
		def locator = mockLocator("void method(string a, ?int b, ?string c)")
		def context = AstTestUtils.parser(locator).methodDefinition()
		def resolver = Mock(TypeResolver)
		def method = Mock(MethodNodeInternal)
		def params = Mock(NamedNodeSetInternal)

		when:
		MethodParser.parseMethodDefinition(locator, resolver, context, method)

		then:
		_ * method.typeParameters >> NodeSets.newNamedNodeSet("type parameters")
		_ * method.parameters >> params
		1 * method.setReturnType({ it == VoidTypeReference.VOID })
		1 * params.add({
			it instanceof MethodParameterNode &&
					it.name == "a" &&
					it.type == PrimitiveTypeReference.STRING &&
					it.optional == false}, _)
		1 * params.add({
			it instanceof MethodParameterNode &&
					it.name == "b" &&
					it.type == PrimitiveTypeReference.INT &&
					it.optional == true}, _)
		1 * params.add({
			it instanceof MethodParameterNode &&
					it.name == "c" &&
					it.type == PrimitiveTypeReference.STRING &&
					it.optional == true}, _)
		0 * _
	}

	def "parse wrong optional"() {
		def locator = mockLocator("void method(?int a, int b, ?int c)")
		def context = AstTestUtils.parser(locator).methodDefinition()
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
		ex.message == " at line 1:20: Only the last parameters of a method can be optional"
	}
}
