package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.TypeReference;
import com.prezi.spaghetti.ast.internal.DefaultMethodNode;
import com.prezi.spaghetti.ast.internal.DefaultMethodParameterNode;
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterNode;
import com.prezi.spaghetti.ast.internal.MutableMethodNode;
import com.prezi.spaghetti.internal.grammar.ModuleParser;
import org.antlr.v4.runtime.tree.TerminalNode;

public class MethodParser {
	protected static DefaultMethodNode parseMethodDefinition(TypeResolver resolver, ModuleParser.MethodDefinitionContext methodCtx) {
		String name = methodCtx.Name().getText();
		DefaultMethodNode methodNode = new DefaultMethodNode(name);
		AnnotationsParser.parseAnnotations(methodCtx.annotations(), methodNode);
		DocumentationParser.parseDocumentation(methodCtx.documentation, methodNode);
		MethodParser.parseMethodDefinition(resolver, methodCtx, methodNode);
		return methodNode;
	}

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
				ModuleParser.TypeNamePairContext pairCtx = paramCtx.typeNamePair();
				String name = pairCtx.Name().getText();
				ModuleParser.ComplexTypeContext typeCtx = pairCtx.complexType();
				TypeReference type = TypeParsers.parseComplexType(resolver, typeCtx);
				boolean optional = paramCtx.optional != null;

				if (previousParameterWasOptional && !optional) {
					throw new InternalAstParserException(paramCtx, "Only the last parameters of a method can be optional");
				}

				DefaultMethodParameterNode paramNode = new DefaultMethodParameterNode(name, type, optional);
				AnnotationsParser.parseAnnotations(paramCtx.annotations(), paramNode);
				methodNode.getParameters().add(paramNode, paramCtx);

				previousParameterWasOptional = optional;
			}
		}

		return methodNode;
	}
}
