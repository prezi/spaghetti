package com.prezi.spaghetti.ast.parser;

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
			for (ModuleParser.MethodParameterContext paramCtx : methodParameters.methodParameter()) {
				ModuleParser.TypeNamePairContext pairCtx = paramCtx.typeNamePair();
				String name = pairCtx.Name().getText();
				ModuleParser.ComplexTypeContext typeCtx = pairCtx.complexType();
				TypeReference type = TypeParsers.parseComplexType(resolver, typeCtx);

				DefaultMethodParameterNode paramNode = new DefaultMethodParameterNode(name, type);
				AnnotationsParser.parseAnnotations(paramCtx.annotations(), paramNode);
				methodNode.getParameters().add(paramNode, paramCtx);
			}
		}

		return methodNode;
	}

}
