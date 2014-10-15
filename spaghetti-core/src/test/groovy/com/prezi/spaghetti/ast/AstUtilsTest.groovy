package com.prezi.spaghetti.ast

import com.prezi.spaghetti.ast.internal.DefaultInterfaceNode
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterNode
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterReference

import static com.prezi.spaghetti.ast.AstUtils.resolveTypeParameters
import static com.prezi.spaghetti.ast.internal.parser.AstTestUtils.parser
import static com.prezi.spaghetti.ast.internal.parser.AstTestUtils.resolver
import static com.prezi.spaghetti.ast.internal.parser.TypeParsers.parseType
import static com.prezi.spaghetti.ast.internal.parser.TypeParsers.parseTypeChain

class AstUtilsTest extends AstTestBase {
	def "resolveTypeParameters simple"() {
		def type = parseType(resolver(), parser("int").type())
		expect:
		resolveTypeParameters(type, [:]) == PrimitiveTypeReference.INT
	}

	def "resolveTypeParameters chain"() {
		def paramT = new DefaultTypeParameterNode("T")
		def bindings = [
				(paramT): PrimitiveTypeReference.STRING
		]

		def type = parseTypeChain(resolver(paramT), parser("int->T->void").typeChain())
		TypeChain result = (TypeChain) resolveTypeParameters(type, bindings)

		expect:
		result.elements == [
				PrimitiveTypeReference.INT,
				PrimitiveTypeReference.STRING,
				VoidTypeReference.VOID
		]
	}

	def "resolveTypeParameters two levels"() {
		// T -> U -> string
		def paramT = new DefaultTypeParameterNode("T")
		def paramU = new DefaultTypeParameterNode("U")
		def iface = new DefaultInterfaceNode(FQName.fromString("com.example.test.Iface"))
		//noinspection GrDeprecatedAPIUsage
		iface.getTypeParameters().add(paramT);
		def bindings = [
				(paramT): new DefaultTypeParameterReference(paramU, 0),
				(paramU): PrimitiveTypeReference.STRING,
		]

		def type = parseType(resolver(paramT, iface), parser("com.example.test.Iface<T>").type())
		InterfaceReference result = (InterfaceReference) resolveTypeParameters(type, bindings)

		expect:
		result.type == iface
		result.arguments == [
				PrimitiveTypeReference.STRING
		]
	}

	// See https://github.com/prezi/spaghetti/issues/143
	def "resolveTypeParameters with array"() {
		// T[] -> U[]
		def paramT = new DefaultTypeParameterNode("T")
		def paramU = new DefaultTypeParameterNode("U")
		def iface = new DefaultInterfaceNode(FQName.fromString("com.example.test.Iface"))
		//noinspection GrDeprecatedAPIUsage
		iface.getTypeParameters().add(paramT);
		def bindings = [
				(paramT): new DefaultTypeParameterReference(paramU, 0)
		]

		def type = parseType(resolver(paramT, iface), parser("com.example.test.Iface<T[]>").type())
		InterfaceReference result = (InterfaceReference) resolveTypeParameters(type, bindings)

		expect:
		result.type == iface
		result.arguments == [
				new DefaultTypeParameterReference(paramU, 1)
		]
	}

	def "resolveTypeParameters unbound"() {
		// T -> U
		def paramT = new DefaultTypeParameterNode("T")
		def paramU = new DefaultTypeParameterNode("U")
		def bindings = [
				(paramT): new DefaultTypeParameterReference(paramU, 0)
		]

		def type = parseTypeChain(resolver(paramT), parser("int->T->void").typeChain())
		TypeChain result = (TypeChain) resolveTypeParameters(type, bindings)

		expect:
		result.elements[0] == PrimitiveTypeReference.INT
		result.elements[2] == VoidTypeReference.VOID
		((TypeParameterReference) result.elements[1]).type == paramU
	}
}
