package com.prezi.spaghetti.ast

import com.prezi.spaghetti.ast.internal.DefaultInterfaceNode
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterNode
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterReference

import static com.prezi.spaghetti.ast.internal.TypeParameterResolver.resolveTypeParameters
import static com.prezi.spaghetti.ast.internal.parser.AstTestUtils.parser
import static com.prezi.spaghetti.ast.internal.parser.AstTestUtils.resolver
import static com.prezi.spaghetti.ast.internal.parser.TypeParsers.parseType
import static com.prezi.spaghetti.ast.internal.parser.TypeParsers.parseTypeChain

class TypeParameterResolverTest extends AstTestBase {
	def "resolveTypeParameters simple"() {
		def locator = mockLocator("int")
		def type = parseType(locator, resolver(), parser(locator).type())
		expect:
		resolveTypeParameters(type, [:]) == PrimitiveTypeReference.INT
	}

	def "resolveTypeParameters chain"() {
		def paramT = new DefaultTypeParameterNode(mockLoc, "T")
		def bindings = [
				(paramT): PrimitiveTypeReference.STRING
		]

		def locator = mockLocator("int->T->void")
		def type = parseTypeChain(locator, resolver(paramT), parser(locator).typeChain())
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
		def paramT = new DefaultTypeParameterNode(mockLoc, "T")
		def paramU = new DefaultTypeParameterNode(mockLoc, "U")
		def iface = new DefaultInterfaceNode(mockLoc, FQName.fromString("com.example.test.Iface"))
		iface.getTypeParameters().addInternal(paramT);
		def bindings = [
				(paramT): new DefaultTypeParameterReference(mockLoc, paramU, 0),
				(paramU): PrimitiveTypeReference.STRING,
		]

		def locator = mockLocator("com.example.test.Iface<T>")
		def type = parseType(locator, resolver(paramT, iface), parser(locator).type())
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
		def paramT = new DefaultTypeParameterNode(mockLoc, "T")
		def paramU = new DefaultTypeParameterNode(mockLoc, "U")
		def iface = new DefaultInterfaceNode(mockLoc, FQName.fromString("com.example.test.Iface"))
		iface.getTypeParameters().addInternal(paramT);
		def bindings = [
				(paramT): new DefaultTypeParameterReference(mockLoc, paramU, 0)
		]

		def locator = mockLocator("com.example.test.Iface<T[]>")
		def type = parseType(locator, resolver(paramT, iface), parser(locator).type())
		InterfaceReference result = (InterfaceReference) resolveTypeParameters(type, bindings)

		expect:
		result.type == iface
		result.arguments == [
				new DefaultTypeParameterReference(mockLoc, paramU, 1)
		]
	}

	def "resolveTypeParameters unbound"() {
		// T -> U
		def paramT = new DefaultTypeParameterNode(mockLoc, "T")
		def paramU = new DefaultTypeParameterNode(mockLoc, "U")
		def bindings = [
				(paramT): new DefaultTypeParameterReference(mockLoc, paramU, 0)
		]

		def locator = mockLocator("int->T->void")
		def type = parseTypeChain(locator, resolver(paramT), parser(locator).typeChain())
		TypeChain result = (TypeChain) resolveTypeParameters(type, bindings)

		expect:
		result.elements[0] == PrimitiveTypeReference.INT
		result.elements[2] == VoidTypeReference.VOID
		((TypeParameterReference) result.elements[1]).type == paramU
	}
}
