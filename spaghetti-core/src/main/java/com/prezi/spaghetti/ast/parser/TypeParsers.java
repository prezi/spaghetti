package com.prezi.spaghetti.ast.parser;

import com.google.common.collect.Lists;
import com.prezi.spaghetti.ast.EnumNode;
import com.prezi.spaghetti.ast.ExternInterfaceNode;
import com.prezi.spaghetti.ast.ExternInterfaceReference;
import com.prezi.spaghetti.ast.InterfaceNode;
import com.prezi.spaghetti.ast.InterfaceReference;
import com.prezi.spaghetti.ast.ParametrizedReferableTypeNode;
import com.prezi.spaghetti.ast.PrimitiveType;
import com.prezi.spaghetti.ast.PrimitiveTypeReference;
import com.prezi.spaghetti.ast.StructNode;
import com.prezi.spaghetti.ast.TypeChain;
import com.prezi.spaghetti.ast.TypeNode;
import com.prezi.spaghetti.ast.TypeNodeReference;
import com.prezi.spaghetti.ast.TypeParameterNode;
import com.prezi.spaghetti.ast.TypeReference;
import com.prezi.spaghetti.ast.VoidTypeReference;
import com.prezi.spaghetti.ast.internal.AbstractParametrizedTypeNodeReference;
import com.prezi.spaghetti.ast.internal.DefaultEnumReference;
import com.prezi.spaghetti.ast.internal.DefaultExternInterfaceReference;
import com.prezi.spaghetti.ast.internal.DefaultInterfaceReference;
import com.prezi.spaghetti.ast.internal.DefaultPrimitiveTypeReference;
import com.prezi.spaghetti.ast.internal.DefaultStructReference;
import com.prezi.spaghetti.ast.internal.DefaultTypeChain;
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterReference;
import com.prezi.spaghetti.grammar.ModuleParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

public class TypeParsers {
	public static TypeReference parseReturnType(TypeResolver resolver, ModuleParser.ReturnTypeContext context) {
		if (context.voidType() != null) {
			return VoidTypeReference.VOID;
		} else if (context.complexType() != null) {
			return parseComplexType(resolver, context.complexType());
		} else {
			throw new InternalAstParserException(context, "Unknown return type");
		}
	}

	public static TypeReference parseComplexType(TypeResolver resolver, ModuleParser.ComplexTypeContext context) {
		if (context.type() != null) {
			return parseType(resolver, context.type());
		} else if (context.typeChain() != null) {
			return parseTypeChain(resolver, context.typeChain());
		} else {
			throw new InternalAstParserException(context, "Unknown complex type");
		}
	}

	public static TypeReference parseType(TypeResolver resolver, ModuleParser.TypeContext context) {
		Integer dimensions = context.ArrayQualifier().size();
		if (context.primitiveType() != null) {
			return parsePrimitiveType(context.primitiveType(), dimensions);
		} else if (context.objectType() != null) {
			return parseObjectType(resolver, context.objectType(), dimensions);
		} else {
			throw new InternalAstParserException(context, "Unknown type");
		}
	}

	public static TypeChain parseTypeChain(TypeResolver resolver, ModuleParser.TypeChainContext context) {
		if (context.typeChainElements() != null) {
			List<TerminalNode> arrayQualifiers = context.ArrayQualifier();
			return parseTypeChainElements(resolver, context.typeChainElements(), arrayQualifiers != null ? arrayQualifiers.size() : 0);
		} else {
			throw new InternalAstParserException(context, "Unknown type chain");
		}
	}

	public static TypeChain parseTypeChainElements(final TypeResolver resolver, ModuleParser.TypeChainElementsContext context, int dimensions) {
		final DefaultTypeChain chain = new DefaultTypeChain(dimensions);
		if (context.voidType() != null) {
			chain.getElements().add(VoidTypeReference.VOID);
		} else {
			for (ModuleParser.TypeChainElementContext elemCtx : context.typeChainElement()) {
				chain.getElements().add(parseTypeChainElement(resolver, elemCtx));
			}
		}

		chain.getElements().add(parseTypeChainReturnType(resolver, context.typeChainReturnType()));
		return chain;
	}

	public static TypeReference parseTypeChainReturnType(TypeResolver resolver, ModuleParser.TypeChainReturnTypeContext context) {
		if (context.voidType() != null) {
			return VoidTypeReference.VOID;
		} else if (context.typeChainElement() != null) {
			return parseTypeChainElement(resolver, context.typeChainElement());
		} else {
			throw new InternalAstParserException(context, "Unknown return type chain element");
		}
	}

	public static TypeReference parseTypeChainElement(TypeResolver resolver, ModuleParser.TypeChainElementContext context) {
		if (context.type() != null) {
			return parseType(resolver, context.type());
		} else if (context.typeChain() != null) {
			return parseTypeChain(resolver, context.typeChain());
		} else {
			throw new InternalAstParserException(context, "Unknown type chain element");
		}
	}

	public static TypeReference parseObjectType(TypeResolver resolver, ModuleParser.ObjectTypeContext context, int dimensions) {
		ModuleParser.QualifiedNameContext name = context.qualifiedName();
		TypeResolutionContext resContext = TypeResolutionContext.create(name);
		TypeNode type = resolver.resolveType(resContext);

		TypeNodeReference result;
		if (type instanceof InterfaceNode) {
			result = parseInterfaceReference(resolver, context, context.typeArguments(), (InterfaceNode) type, dimensions);
		} else if (type instanceof ExternInterfaceNode) {
			result = parseExternInterfaceReference(resolver, context, context.typeArguments(), (ExternInterfaceNode) type, dimensions);
		} else if (type instanceof StructNode) {
			checkTypeArguments(context.typeArguments(), "Struct");
			result = new DefaultStructReference((StructNode) type, dimensions);
		} else if (type instanceof EnumNode) {
			checkTypeArguments(context.typeArguments(), "Enum");
			result = new DefaultEnumReference((EnumNode) type, dimensions);
		} else if (type instanceof TypeParameterNode) {
			checkTypeArguments(context.typeArguments(), "Type parameter");
			result = new DefaultTypeParameterReference((TypeParameterNode) type, dimensions);
		} else {
			throw new InternalAstParserException(name, "Unknown type reference");
		}

		return result;
	}

	protected static void checkTypeArguments(ModuleParser.TypeArgumentsContext context, final String what) {
		if (context != null && context.returnType() != null) {
			throw new InternalAstParserException(context, what + " cannot accept type arguments");
		}
	}

	protected static InterfaceReference parseInterfaceReference(TypeResolver resolver, ParserRuleContext typeCtx, ModuleParser.TypeArgumentsContext argsCtx, InterfaceNode type, int arrayDimensions) {
		DefaultInterfaceReference ifaceRef = new DefaultInterfaceReference(type, arrayDimensions);
		return parseParametrizedTypeNodeReference(ifaceRef, resolver, typeCtx, argsCtx, type);
	}

	protected static ExternInterfaceReference parseExternInterfaceReference(TypeResolver resolver, ParserRuleContext typeCtx, ModuleParser.TypeArgumentsContext argsCtx, ExternInterfaceNode type, int arrayDimensions) {
		DefaultExternInterfaceReference ifaceRef = new DefaultExternInterfaceReference(type, arrayDimensions);
		return parseParametrizedTypeNodeReference(ifaceRef, resolver, typeCtx, argsCtx, type);
	}

	private static <T extends ParametrizedReferableTypeNode, R extends AbstractParametrizedTypeNodeReference<T>> R parseParametrizedTypeNodeReference(R reference, TypeResolver resolver, ParserRuleContext typeCtx, ModuleParser.TypeArgumentsContext argsCtx, T type) {
		List<TypeReference> arguments = Lists.newArrayList();

		if (argsCtx != null) {
			List<ModuleParser.ReturnTypeContext> returnTypeCtx = argsCtx.returnType();
			if (returnTypeCtx != null) {
				for (ModuleParser.ReturnTypeContext argCtx : returnTypeCtx) {
					arguments.add(parseReturnType(resolver, argCtx));
				}
			}
		}

		if (arguments.size() != type.getTypeParameters().size()) {
			throw new InternalAstParserException(typeCtx, "Type argument count doesn't match number of type parameters");
		}

		reference.getArguments().addAll(arguments);
		return reference;
	}

	public static PrimitiveTypeReference parsePrimitiveType(ModuleParser.PrimitiveTypeContext typeCtx, int arrayDimensions) {
		PrimitiveType type;
		if (typeCtx.boolType() != null) {
			type = PrimitiveType.BOOL;
		} else if (typeCtx.intType() != null) {
			type = PrimitiveType.INT;
		} else if (typeCtx.floatType() != null) {
			type = PrimitiveType.FLOAT;
		} else if (typeCtx.stringType() != null) {
			type = PrimitiveType.STRING;
		} else if (typeCtx.anyType() != null) {
			type = PrimitiveType.ANY;
		} else {
			throw new InternalAstParserException(typeCtx, "Unknown primitive type");
		}

		return new DefaultPrimitiveTypeReference(type, arrayDimensions);
	}
}
