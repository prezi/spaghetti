package com.prezi.spaghetti.ast.parser;

import com.prezi.spaghetti.ast.ModuleMethodType;
import com.prezi.spaghetti.ast.TypeReference;
import com.prezi.spaghetti.ast.internal.DefaultMethodParameterNode;
import com.prezi.spaghetti.ast.internal.DefaultModuleMethodNode;
import com.prezi.spaghetti.ast.internal.DefaultTypeMethodNode;
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterNode;
import com.prezi.spaghetti.ast.internal.MutableMethodNode;
import com.prezi.spaghetti.grammar.ModuleParser;
import org.antlr.v4.runtime.tree.TerminalNode;

public class MethodParser {
	protected static DefaultModuleMethodNode parseModuleMethodDefinition(TypeResolver resolver, ModuleParser.ModuleMethodDefinitionContext methodCtx) {
		String name = methodCtx.methodDefinition().Name().getText();
		ModuleMethodType type = methodCtx.isStatic != null ? ModuleMethodType.STATIC : ModuleMethodType.DYNAMIC;
		DefaultModuleMethodNode methodNode = new DefaultModuleMethodNode(name, type);
		AnnotationsParser.parseAnnotations(methodCtx.annotations(), methodNode);
		DocumentationParser.parseDocumentation(methodCtx.documentation, methodNode);
		MethodParser.parseMethodDefinition(resolver, methodCtx.methodDefinition(), methodNode);
		return methodNode;
	}

	protected static DefaultTypeMethodNode parseTypeMethodDefinition(TypeResolver resolver, ModuleParser.TypeMethodDefinitionContext methodCtx) {
		String name = methodCtx.methodDefinition().Name().getText();
		DefaultTypeMethodNode methodNode = new DefaultTypeMethodNode(name);
		AnnotationsParser.parseAnnotations(methodCtx.annotations(), methodNode);
		DocumentationParser.parseDocumentation(methodCtx.documentation, methodNode);
		parseMethodDefinition(resolver, methodCtx.methodDefinition(), methodNode);
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
