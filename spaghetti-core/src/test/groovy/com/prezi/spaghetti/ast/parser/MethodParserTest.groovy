package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.MethodParameterNode
import com.prezi.spaghetti.ast.NamedNodeSet
import com.prezi.spaghetti.ast.PrimitiveTypeReference
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.ast.TypeParameterNode
import com.prezi.spaghetti.ast.internal.DefaultNamedNodeSet
import com.prezi.spaghetti.ast.internal.MutableMethodNode
import spock.lang.Specification

class MethodParserTest extends Specification {
	def "parse simple"() {
		def context = AstTestUtils.parser("int add(int a, int b)").methodDefinition()
		def resolver = Mock(TypeResolver)
		def method = Mock(MutableMethodNode)
		def params = Mock(NamedNodeSet)

		when:
		MethodParser.parseMethodDefinition(resolver, context, method)

		then:
		_ * method.typeParameters >> new DefaultNamedNodeSet<>("type parameters")
		_ * method.parameters >> params
		1 * method.setReturnType({ it == PrimitiveTypeReference.INT })
		1 * params.add({ it instanceof MethodParameterNode && it.name == "a" && it.type == PrimitiveTypeReference.INT }, _)
		1 * params.add({ it instanceof MethodParameterNode && it.name == "b" && it.type == PrimitiveTypeReference.INT }, _)
		0 * _
	}

	def "parse struct and interface"() {
		def context = AstTestUtils.parser("StructA method(MyInterface i)").methodDefinition()
		def mockStructA = Mock(StructNode)
		def mockIfaceI = Mock(InterfaceNode)
		def resolver = Mock(TypeResolver)
		def method = Mock(MutableMethodNode)
		def params = Mock(NamedNodeSet)

		when:
		MethodParser.parseMethodDefinition(resolver, context, method)

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
		_ * method.typeParameters >> new DefaultNamedNodeSet<>("type parameters")
		_ * method.parameters >> params
		_ * mockIfaceI.typeParameters >> new DefaultNamedNodeSet<>("type parameters")
		1 * method.setReturnType({ it.type == mockStructA })
		1 * params.add({ it instanceof MethodParameterNode && it.name == "i" && it.type.type == mockIfaceI }, _)
		0 * _
	}

	def "parse generic"() {
		def context = AstTestUtils.parser("T method(T t)").methodDefinition()
		def typeParam = Mock(TypeParameterNode)
		def resolver = Mock(TypeResolver)
		def method = Mock(MutableMethodNode)
		def params = Mock(NamedNodeSet)

		when:
		MethodParser.parseMethodDefinition(resolver, context, method)

		then:
		_ * method.typeParameters >> new DefaultNamedNodeSet<>("type parameter", [ typeParam ].toSet())
		_ * typeParam.qualifiedName >> FQName.fromString("T")
		_ * method.parameters >> params
		1 * method.setReturnType({ it.type == typeParam })
		1 * params.add({ it instanceof MethodParameterNode && it.name == "t" && it.type.type == typeParam }, _)
		0 * _
	}
}
