package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.ExternNode
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.InterfaceReference
import com.prezi.spaghetti.ast.PrimitiveType
import com.prezi.spaghetti.ast.PrimitiveTypeReference
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.ast.TypeChain
import com.prezi.spaghetti.ast.TypeNodeReference
import com.prezi.spaghetti.ast.TypeParameterNode
import com.prezi.spaghetti.ast.TypeReference
import com.prezi.spaghetti.ast.VoidTypeReference
import com.prezi.spaghetti.ast.internal.DefaultEnumReference
import com.prezi.spaghetti.ast.internal.DefaultExternReference
import com.prezi.spaghetti.ast.internal.DefaultInterfaceReference
import com.prezi.spaghetti.ast.internal.DefaultPrimitiveTypeReference
import com.prezi.spaghetti.ast.internal.DefaultStructReference
import com.prezi.spaghetti.ast.internal.DefaultTypeChain
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterReference
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.ParserRuleContext

/**
 * Created by lptr on 29/05/14.
 */
class TypeParsers {

	static TypeReference parseReturnType(TypeResolver resolver, ModuleParser.ReturnTypeContext context) {
		if (context.voidType()) {
			return VoidTypeReference.VOID
		} else if (context.complexType()) {
			return parseComplexType(resolver, context.complexType())
		} else {
			throw new InternalAstParserException(context, "Unknown return type")
		}
	}

	static TypeReference parseComplexType(TypeResolver resolver, ModuleParser.ComplexTypeContext context) {
		if (context.type()) {
			return parseType(resolver, context.type())
		} else if (context.typeChain()) {
			return parseTypeChain(resolver, context.typeChain())
		} else {
			throw new InternalAstParserException(context, "Unknown complex type")
		}
	}

	static TypeReference parseType(TypeResolver resolver, ModuleParser.TypeContext context) {
		def dimensions = context.ArrayQualifier().size()
		if (context.primitiveType()) {
			return parsePrimitiveType(context.primitiveType(), dimensions)
		} else if (context.objectType()) {
			return parseObjectType(resolver, context.objectType(), dimensions)
		} else {
			throw new InternalAstParserException(context, "Unknown type")
		}
	}

	static TypeChain parseTypeChain(TypeResolver resolver, ModuleParser.TypeChainContext context) {
		if (context.typeChainElements()) {
			return parseTypeChainElements(resolver, context.typeChainElements(), context.ArrayQualifier()?.size())
		} else {
			throw new InternalAstParserException(context, "Unknown type chain")
		}
	}

	static TypeChain parseTypeChainElements(TypeResolver resolver, ModuleParser.TypeChainElementsContext context, int dimensions) {
		def chain = new DefaultTypeChain(dimensions)
		if (context.voidType()) {
			chain.elements.add VoidTypeReference.VOID
		} else {
			context.typeChainElement().each { elemCtx ->
				chain.elements.add parseTypeChainElement(resolver, elemCtx)
			}
		}
		chain.elements.add parseTypeChainReturnType(resolver, context.typeChainReturnType())
		return chain
	}

	static TypeReference parseTypeChainReturnType(TypeResolver resolver, ModuleParser.TypeChainReturnTypeContext context) {
		if (context.voidType()) {
			return VoidTypeReference.VOID
		} else if (context.typeChainElement()) {
			return parseTypeChainElement(resolver, context.typeChainElement())
		} else {
			throw new InternalAstParserException(context, "Unknown return type chain element")
		}
	}

	static TypeReference parseTypeChainElement(TypeResolver resolver, ModuleParser.TypeChainElementContext context) {
		if (context.type()) {
			return parseType(resolver, context.type())
		} else if (context.typeChain()) {
			return parseTypeChain(resolver, context.typeChain())
		} else {
			throw new InternalAstParserException(context, "Unknown type chain element")
		}
	}

	static TypeReference parseObjectType(TypeResolver resolver, ModuleParser.ObjectTypeContext context, int dimensions) {
		def name = context.qualifiedName()
		def resContext = TypeResolutionContext.create(name)
		def type = resolver.resolveType(resContext)

		TypeNodeReference result
		if (type instanceof InterfaceNode) {
			result = parseInterfaceReference(resolver, context, context.typeArguments(), type, dimensions)
		} else if (type instanceof StructNode) {
			checkTypeArguments(context.typeArguments(), "Struct")
			result = new DefaultStructReference(type, dimensions)
		} else if (type instanceof EnumNode) {
			checkTypeArguments(context.typeArguments(), "Enum")
			result = new DefaultEnumReference(type, dimensions)
		} else if (type instanceof TypeParameterNode) {
			checkTypeArguments(context.typeArguments(), "Type parameter")
			result = new DefaultTypeParameterReference(type, dimensions)
		} else if (type instanceof ExternNode) {
			checkTypeArguments(context.typeArguments(), "Extern")
			result = new DefaultExternReference(type, dimensions)
		} else {
			throw new InternalAstParserException(name, "Unknown type reference")
		}
		return result
	}

	protected static void checkTypeArguments(ModuleParser.TypeArgumentsContext context, String what) {
		if (context?.returnType()) {
			throw new InternalAstParserException(context, "${what} cannot accept type arguments")
		}
	}

	static
	protected InterfaceReference parseInterfaceReference(TypeResolver resolver, ParserRuleContext typeCtx, ModuleParser.TypeArgumentsContext argsCtx, InterfaceNode type, int arrayDimensions) {
		def ifaceRef = new DefaultInterfaceReference(type, arrayDimensions)
		def arguments = []
		argsCtx?.returnType()?.each { ModuleParser.ReturnTypeContext argCtx ->
			arguments.add(parseReturnType(resolver, argCtx))
		}
		if (arguments.size() != type.typeParameters.size()) {
			throw new InternalAstParserException(typeCtx, "Interface argument count don't match")
		}
		ifaceRef.arguments.addAll arguments
		return ifaceRef
	}

	static PrimitiveTypeReference parsePrimitiveType(ModuleParser.PrimitiveTypeContext typeCtx, int arrayDimensions) {
		PrimitiveType type
		if (typeCtx.boolType()) {
			type = PrimitiveType.BOOL
		} else if (typeCtx.intType()) {
			type = PrimitiveType.INT
		} else if (typeCtx.floatType()) {
			type = PrimitiveType.FLOAT
		} else if (typeCtx.stringType()) {
			type = PrimitiveType.STRING
		} else if (typeCtx.anyType()) {
			type = PrimitiveType.ANY
		} else {
			throw new InternalAstParserException(typeCtx, "Unknown primitive type")
		}
		return new DefaultPrimitiveTypeReference(type, arrayDimensions)
	}
}
