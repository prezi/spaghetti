package com.prezi.spaghetti.ast.parser;

import com.prezi.spaghetti.ast.PrimitiveTypeReference;
import com.prezi.spaghetti.ast.TypeReference;
import com.prezi.spaghetti.ast.internal.DefaultMethodParameterNode;
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterNode;
import com.prezi.spaghetti.ast.internal.MutableMethodNode;
import com.prezi.spaghetti.grammar.ModuleParser;
import org.antlr.v4.runtime.tree.TerminalNode;

public class MethodParser {
	protected static <T extends MutableMethodNode> T parseMethodDefinition(TypeResolver resolver, ModuleParser.MethodDefinitionContext methodCtx, T methodNode) {
		ModuleParser.TypeParametersContext typeParameters = methodCtx.typeParameters();
		if (typeParameters != null) {
			for (TerminalNode name : typeParameters.Name()) {
				methodNode.getTypeParameters().add(new DefaultTypeParameterNode(name.getText()), name);
			}
		}

		// Let further processing access type parameters as defined types
		resolver = new SimpleNamedTypeResolver(resolver, methodNode.getTypeParameters());

		methodNode.setReturnType(TypeParsers.parseReturnType(resolver, methodCtx.returnType()));

		ModuleParser.MethodParametersContext methodParameters = methodCtx.methodParameters();
		if (methodParameters != null) {
			boolean previousParameterWasOptional = false;
			for (ModuleParser.MethodParameterContext paramCtx : methodParameters.methodParameter()) {
				String name;
				TypeReference type;
				boolean optional;
				Object optionalValue;
				if (paramCtx.typeNamePair() != null) {
					if (previousParameterWasOptional) {
						throw new InternalAstParserException(paramCtx, "Only the last parameters of a method can be optional");
					}
					ModuleParser.TypeNamePairContext pairCtx = paramCtx.typeNamePair();
					name = pairCtx.Name().getText();
					ModuleParser.ComplexTypeContext typeCtx = pairCtx.complexType();
					type = TypeParsers.parseComplexType(resolver, typeCtx);
					optional = false;
					optionalValue = null;
				} else if (paramCtx.optionalMethodParameterDecl() != null) {
					ModuleParser.OptionalMethodParameterDeclContext optionalCtx = paramCtx.optionalMethodParameterDecl();
					name = optionalCtx.Name().getText();
					optional = true;

					if (optionalCtx.Boolean() != null) {
						type = PrimitiveTypeReference.BOOL;
						optionalValue = Primitives.parseBoolean(optionalCtx.Boolean().getSymbol());
					} else if (optionalCtx.Integer() != null) {
						type = PrimitiveTypeReference.INT;
						optionalValue = Primitives.parseInt(optionalCtx.Integer().getSymbol());
					} else if (optionalCtx.Float() != null) {
						type = PrimitiveTypeReference.FLOAT;
						optionalValue = Primitives.parseDouble(optionalCtx.Float().getSymbol());
					} else if (optionalCtx.String() != null) {
						type = PrimitiveTypeReference.STRING;
						optionalValue = Primitives.parseString(optionalCtx.String().getSymbol());
					} else if (optionalCtx.complexType() != null) {
						type = TypeParsers.parseComplexType(resolver, optionalCtx.complexType());
						optionalValue = null;
					} else {
						throw new InternalAstParserException(optionalCtx, "Unknown optional type");
					}
					previousParameterWasOptional = true;
				} else {
					throw new InternalAstParserException(paramCtx, "Unknown method parameter type");
				}
				DefaultMethodParameterNode paramNode = new DefaultMethodParameterNode(name, type, optional, optionalValue);
				AnnotationsParser.parseAnnotations(paramCtx.annotations(), paramNode);
				methodNode.getParameters().add(paramNode, paramCtx);
			}
		}

		return methodNode;
	}
}
