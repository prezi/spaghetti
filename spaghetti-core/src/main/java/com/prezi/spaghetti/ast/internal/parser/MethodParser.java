package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.TypeReference;
import com.prezi.spaghetti.ast.internal.DefaultMethodNode;
import com.prezi.spaghetti.ast.internal.DefaultMethodParameterNode;
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterNode;
import com.prezi.spaghetti.ast.internal.MethodNodeInternal;
import com.prezi.spaghetti.internal.grammar.ModuleParser;
import org.antlr.v4.runtime.tree.TerminalNode;

public class MethodParser {
	protected static DefaultMethodNode parseMethodDefinition(Locator locator, TypeResolver resolver, ModuleParser.MethodDefinitionContext methodCtx) {
		String name = methodCtx.Name().getText();
		DefaultMethodNode methodNode = new DefaultMethodNode(locator.locate(methodCtx.Name()), name);
		AnnotationsParser.parseAnnotations(locator, methodCtx.annotations(), methodNode);
		DocumentationParser.parseDocumentation(locator, methodCtx.documentation, methodNode);
		MethodParser.parseMethodDefinition(locator, resolver, methodCtx, methodNode);
		return methodNode;
	}

	protected static <T extends MethodNodeInternal> T parseMethodDefinition(Locator locator, TypeResolver resolver, ModuleParser.MethodDefinitionContext methodCtx, T methodNode) {
		ModuleParser.TypeParametersContext typeParameters = methodCtx.typeParameters();
		if (typeParameters != null) {
			for (TerminalNode name : typeParameters.Name()) {
				methodNode.getTypeParameters().add(new DefaultTypeParameterNode(locator.locate(name), name.getText()), name);
			}
		}

		// Let further processing access type parameters as defined types
		resolver = new SimpleNamedTypeResolver(resolver, methodNode.getTypeParameters());

		methodNode.setReturnType(TypeParsers.parseReturnType(locator, resolver, methodCtx.returnType()));

		ModuleParser.MethodParametersContext methodParameters = methodCtx.methodParameters();
		if (methodParameters != null) {
			boolean previousParameterWasOptional = false;
			for (ModuleParser.MethodParameterContext paramCtx : methodParameters.methodParameter()) {
				ModuleParser.TypeNamePairContext pairCtx = paramCtx.typeNamePair();
				String name = pairCtx.Name().getText();
				ModuleParser.ComplexTypeContext typeCtx = pairCtx.complexType();
				TypeReference type = TypeParsers.parseComplexType(locator, resolver, typeCtx);
				boolean optional = paramCtx.optional != null;

				if (previousParameterWasOptional && !optional) {
					throw new InternalAstParserException(paramCtx, "Only the last parameters of a method can be optional");
				}

				DefaultMethodParameterNode paramNode = new DefaultMethodParameterNode(locator.locate(pairCtx.Name()), name, type, optional);
				AnnotationsParser.parseAnnotations(locator, paramCtx.annotations(), paramNode);
				methodNode.getParameters().add(paramNode, paramCtx);

				previousParameterWasOptional = optional;
			}
		}

		return methodNode;
	}
}
