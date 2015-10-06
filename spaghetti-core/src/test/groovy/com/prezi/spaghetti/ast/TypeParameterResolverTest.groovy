package com.prezi.spaghetti.ast

import com.prezi.spaghetti.ast.internal.DefaultFQName
import com.prezi.spaghetti.ast.internal.DefaultInterfaceNode
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterNode
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterReference
import com.prezi.spaghetti.ast.internal.PrimitiveTypeReferenceInternal
import com.prezi.spaghetti.ast.internal.VoidTypeReferenceInternal
import com.prezi.spaghetti.ast.internal.parser.AstParserSpecification

import static com.prezi.spaghetti.ast.internal.TypeParameterResolver.resolveTypeParameters
import static com.prezi.spaghetti.ast.internal.parser.TypeParsers.parseComplexType
import static com.prezi.spaghetti.ast.internal.parser.TypeParsers.parseFunctionType
import static com.prezi.spaghetti.ast.internal.parser.TypeParsers.parseType
import static com.prezi.spaghetti.ast.internal.parser.TypeParsers.parseTypeChain

class TypeParameterResolverTest extends AstParserSpecification {
	def "resolveTypeParameters simple"() {
		def locator = mockLocator("int")
		def type = parseComplexType(locator, resolver(), parser(locator).complexType())
		expect:
		resolveTypeParameters(type, [:]) == PrimitiveTypeReferenceInternal.INT
	}

	def "resolveTypeParameters chain"() {
		def paramT = new DefaultTypeParameterNode(mockLoc, "T")
		def bindings = [
				(paramT): PrimitiveTypeReferenceInternal.STRING
		]

		def locator = mockLocator("(int, T) -> void")
		def type = parseFunctionType(locator, resolver(paramT), parser(locator).functionType(), 0)
		TypeChain result = (TypeChain) resolveTypeParameters(type, bindings)

		expect:
		result.elements == [
				PrimitiveTypeReferenceInternal.INT,
				PrimitiveTypeReferenceInternal.STRING,
				VoidTypeReferenceInternal.VOID
		]
	}

	def "resolveTypeParameters two levels"() {
		// (T, U) -> string
		def paramT = new DefaultTypeParameterNode(mockLoc, "T")
		def paramU = new DefaultTypeParameterNode(mockLoc, "U")
		def iface = new DefaultInterfaceNode(mockLoc, DefaultFQName.fromString("com.example.test.Iface"))
		iface.getTypeParameters().addInternal(paramT);
		def bindings = [
				(paramT): new DefaultTypeParameterReference(mockLoc, paramU, 0),
				(paramU): PrimitiveTypeReferenceInternal.STRING,
		]

		def locator = mockLocator("com.example.test.Iface<T>")
		def type = parseComplexType(locator, resolver(paramT, iface), parser(locator).complexType())
		InterfaceReference result = (InterfaceReference) resolveTypeParameters(type, bindings)

		expect:
		result.type == iface
		result.arguments == [
				PrimitiveTypeReferenceInternal.STRING
		]
	}

	// See https://github.com/prezi/spaghetti/issues/143
	def "resolveTypeParameters with array"() {
		// (T[]) -> U[]
		def paramT = new DefaultTypeParameterNode(mockLoc, "T")
		def paramU = new DefaultTypeParameterNode(mockLoc, "U")
		def iface = new DefaultInterfaceNode(mockLoc, DefaultFQName.fromString("com.example.test.Iface"))
		iface.getTypeParameters().addInternal(paramT);
		def bindings = [
				(paramT): new DefaultTypeParameterReference(mockLoc, paramU, 0)
		]

		def locator = mockLocator("com.example.test.Iface<T[]>")
		def type = parseComplexType(locator, resolver(paramT, iface), parser(locator).complexType())
		InterfaceReference result = (InterfaceReference) resolveTypeParameters(type, bindings)

		expect:
		result.type == iface
		result.arguments == [
				new DefaultTypeParameterReference(mockLoc, paramU, 1)
		]
	}

	def "resolveTypeParameters unbound"() {
		// (T) -> U
		def paramT = new DefaultTypeParameterNode(mockLoc, "T")
		def paramU = new DefaultTypeParameterNode(mockLoc, "U")
		def bindings = [
				(paramT): new DefaultTypeParameterReference(mockLoc, paramU, 0)
		]

		def locator = mockLocator("(int, T) -> void")
		def type = parseFunctionType(locator, resolver(paramT), parser(locator).functionType(), 0)
		TypeChain result = (TypeChain) resolveTypeParameters(type, bindings)

		expect:
		result.elements[0] == PrimitiveTypeReferenceInternal.INT
		result.elements[2] == VoidTypeReferenceInternal.VOID
		((TypeParameterReference) result.elements[1]).type == paramU
	}
}
