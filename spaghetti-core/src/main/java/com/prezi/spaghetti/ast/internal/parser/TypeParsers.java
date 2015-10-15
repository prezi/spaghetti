package com.prezi.spaghetti.ast.internal.parser;

import com.google.common.collect.Lists;
import com.prezi.spaghetti.ast.EnumNode;
import com.prezi.spaghetti.ast.ExternInterfaceNode;
import com.prezi.spaghetti.ast.InterfaceNode;
import com.prezi.spaghetti.ast.ParametrizedReferableTypeNode;
import com.prezi.spaghetti.ast.PrimitiveType;
import com.prezi.spaghetti.ast.StructNode;
import com.prezi.spaghetti.ast.TypeNode;
import com.prezi.spaghetti.ast.TypeParameterNode;
import com.prezi.spaghetti.ast.internal.AbstractParametrizedTypeNodeReference;
import com.prezi.spaghetti.ast.internal.DefaultEnumReference;
import com.prezi.spaghetti.ast.internal.DefaultExternInterfaceReference;
import com.prezi.spaghetti.ast.internal.DefaultInterfaceReference;
import com.prezi.spaghetti.ast.internal.DefaultPrimitiveTypeReference;
import com.prezi.spaghetti.ast.internal.DefaultStructReference;
import com.prezi.spaghetti.ast.internal.DefaultFunctionType;
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterReference;
import com.prezi.spaghetti.ast.internal.ExternInterfaceReferenceInternal;
import com.prezi.spaghetti.ast.internal.InterfaceReferenceInternal;
import com.prezi.spaghetti.ast.internal.PrimitiveTypeReferenceInternal;
import com.prezi.spaghetti.ast.internal.StructReferenceInternal;
import com.prezi.spaghetti.ast.internal.FunctionTypeInternal;
import com.prezi.spaghetti.ast.internal.TypeNodeReferenceInternal;
import com.prezi.spaghetti.ast.internal.TypeReferenceInternal;
import com.prezi.spaghetti.ast.internal.VoidTypeReferenceInternal;
import com.prezi.spaghetti.internal.grammar.ModuleParser;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class TypeParsers {
	public static TypeReferenceInternal parseReturnType(Locator locator, TypeResolver resolver, ModuleParser.ReturnTypeContext context) {
		if (context.voidType() != null) {
			return VoidTypeReferenceInternal.VOID;
		} else if (context.complexType() != null) {
			return parseComplexType(locator, resolver, context.complexType());
		} else {
			throw new InternalAstParserException(context, "Unknown return type");
		}
	}

	public static TypeReferenceInternal parseComplexType(Locator locator, TypeResolver resolver, ModuleParser.ComplexTypeContext context) {
		Integer dimensions = context.ArrayQualifier() != null ? context.ArrayQualifier().size() : 0;
		if (context.primitiveType() != null) {
			return parsePrimitiveType(locator, context.primitiveType(), dimensions);
		} else if (context.objectType() != null) {
			return parseObjectType(locator, resolver, context.objectType(), dimensions);
		} else if (context.functionType() != null) {
			return parseFunctionType(locator, resolver, context.functionType(), dimensions);
		} else {
			throw new InternalAstParserException(context, "Unknown complex type");
		}
	}

	public static FunctionTypeInternal parseFunctionType(Locator locator, TypeResolver resolver, ModuleParser.FunctionTypeContext context, int dimensions) {
		final DefaultFunctionType functionType = new DefaultFunctionType(locator.locate(context), dimensions);
		if (context.functionParameters() == null) {
			functionType.getElementsInternal().add(VoidTypeReferenceInternal.VOID);
		} else {
			for (ModuleParser.ComplexTypeContext typeCtx : context.functionParameters().complexType()) {
				functionType.getElementsInternal().add(parseComplexType(locator, resolver, typeCtx));
			}
		}
		functionType.getElementsInternal().add(parseReturnType(locator, resolver, context.returnType()));
		return functionType;
	}

	public static TypeReferenceInternal parseObjectType(Locator locator, TypeResolver resolver, ModuleParser.ObjectTypeContext context, int dimensions) {
		ModuleParser.QualifiedNameContext name = context.qualifiedName();
		TypeResolutionContext resContext = TypeResolutionContext.create(name);
		TypeNode type = resolver.resolveType(resContext);

		TypeNodeReferenceInternal result;
		if (type instanceof InterfaceNode) {
			result = parseInterfaceReference(locator, resolver, context, context.typeArguments(), (InterfaceNode) type, dimensions);
		} else if (type instanceof ExternInterfaceNode) {
			result = parseExternInterfaceReference(locator, resolver, context, context.typeArguments(), (ExternInterfaceNode) type, dimensions);
		} else if (type instanceof StructNode) {
			result = parseStructReference(locator, resolver, context, context.typeArguments(), (StructNode) type, dimensions);
		} else if (type instanceof EnumNode) {
			checkTypeArguments(context.typeArguments(), "Enum");
			result = new DefaultEnumReference(locator.locate(context.qualifiedName()), (EnumNode) type, dimensions);
		} else if (type instanceof TypeParameterNode) {
			checkTypeArguments(context.typeArguments(), "Type parameter");
			result = new DefaultTypeParameterReference(locator.locate(context.qualifiedName()), (TypeParameterNode) type, dimensions);
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

	protected static StructReferenceInternal parseStructReference(Locator locator, TypeResolver resolver, ParserRuleContext typeCtx, ModuleParser.TypeArgumentsContext argsCtx, StructNode type, int arrayDimensions) {
		DefaultStructReference ifaceRef = new DefaultStructReference(locator.locate(typeCtx), type, arrayDimensions);
		return parseParametrizedTypeNodeReference(locator, ifaceRef, resolver, typeCtx, argsCtx, type);
	}

	protected static InterfaceReferenceInternal parseInterfaceReference(Locator locator, TypeResolver resolver, ParserRuleContext typeCtx, ModuleParser.TypeArgumentsContext argsCtx, InterfaceNode type, int arrayDimensions) {
		DefaultInterfaceReference ifaceRef = new DefaultInterfaceReference(locator.locate(typeCtx), type, arrayDimensions);
		return parseParametrizedTypeNodeReference(locator, ifaceRef, resolver, typeCtx, argsCtx, type);
	}

	protected static ExternInterfaceReferenceInternal parseExternInterfaceReference(Locator locator, TypeResolver resolver, ParserRuleContext typeCtx, ModuleParser.TypeArgumentsContext argsCtx, ExternInterfaceNode type, int arrayDimensions) {
		DefaultExternInterfaceReference ifaceRef = new DefaultExternInterfaceReference(locator.locate(typeCtx), type, arrayDimensions);
		return parseParametrizedTypeNodeReference(locator, ifaceRef, resolver, typeCtx, argsCtx, type);
	}

	private static <T extends ParametrizedReferableTypeNode, R extends AbstractParametrizedTypeNodeReference<T>> R parseParametrizedTypeNodeReference(Locator locator, R reference, TypeResolver resolver, ParserRuleContext typeCtx, ModuleParser.TypeArgumentsContext argsCtx, T type) {
		List<TypeReferenceInternal> arguments = Lists.newArrayList();

		if (argsCtx != null) {
			List<ModuleParser.ReturnTypeContext> returnTypeCtx = argsCtx.returnType();
			if (returnTypeCtx != null) {
				for (ModuleParser.ReturnTypeContext argCtx : returnTypeCtx) {
					arguments.add(parseReturnType(locator, resolver, argCtx));
				}
			}
		}

		if (arguments.size() != type.getTypeParameters().size()) {
			throw new InternalAstParserException(typeCtx, "Type argument count doesn't match number of type parameters");
		}

		reference.getArgumentsInternal().addAll(arguments);
		return reference;
	}

	public static PrimitiveTypeReferenceInternal parsePrimitiveType(Locator locator, ModuleParser.PrimitiveTypeContext typeCtx, int arrayDimensions) {
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

		return new DefaultPrimitiveTypeReference(locator.locate(typeCtx), type, arrayDimensions);
	}
}
