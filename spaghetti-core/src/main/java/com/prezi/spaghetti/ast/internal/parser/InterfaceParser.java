package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.ExternInterfaceNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.InterfaceNode;
import com.prezi.spaghetti.ast.InterfaceNodeBase;
import com.prezi.spaghetti.ast.InterfaceReferenceBase;
import com.prezi.spaghetti.ast.TypeNode;
import com.prezi.spaghetti.ast.internal.DefaultInterfaceNode;
import com.prezi.spaghetti.ast.internal.DefaultMethodNode;
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterNode;
import com.prezi.spaghetti.grammar.ModuleParser;
import org.antlr.v4.runtime.tree.TerminalNode;

public class InterfaceParser extends AbstractModuleTypeParser<ModuleParser.InterfaceDefinitionContext, InterfaceNode> {
	public InterfaceParser(ModuleParser.InterfaceDefinitionContext context, String moduleName) {
		super(context, createNode(context, moduleName));
	}

	private static InterfaceNode createNode(ModuleParser.InterfaceDefinitionContext context, String moduleName) {
		DefaultInterfaceNode node = new DefaultInterfaceNode(FQName.fromString(moduleName, context.Name().getText()));
		AnnotationsParser.parseAnnotations(context.annotations(), node);
		DocumentationParser.parseDocumentation(context.documentation, node);

		ModuleParser.TypeParametersContext typeParameters = context.typeParameters();
		if (typeParameters != null) {
			for (TerminalNode name : typeParameters.Name()) {
				node.getTypeParameters().add(new DefaultTypeParameterNode(name.getText()), context);
			}
		}

		return node;
	}

	@Override
	public void parse(TypeResolver resolver) {
		// Let further processing access type parameters as defined types
		resolver = new SimpleNamedTypeResolver(resolver, getNode().getTypeParameters());

		for (ModuleParser.SuperInterfaceDefinitionContext superCtx : getContext().superInterfaceDefinition()) {
			getNode().getSuperInterfaces().add(parseSuperInterface(resolver, superCtx));
		}

		for (ModuleParser.MethodDefinitionContext methodCtx : getContext().methodDefinition()) {
			DefaultMethodNode methodNode = MethodParser.parseMethodDefinition(resolver, methodCtx);
			getNode().getMethods().add(methodNode, methodCtx.Name());
		}
	}

	protected static InterfaceReferenceBase parseSuperInterface(TypeResolver resolver, ModuleParser.SuperInterfaceDefinitionContext superCtx) {
		TypeNode superType = resolver.resolveType(TypeResolutionContext.create(superCtx.qualifiedName()));
		if (!(superType instanceof InterfaceNodeBase)) {
			throw new InternalAstParserException(superCtx, "Only interfaces can be super interfaces");
		}

		if (superType instanceof InterfaceNode) {
			return TypeParsers.parseInterfaceReference(resolver, superCtx, superCtx.typeArguments(), (InterfaceNode) superType, 0);
		} else if (superType instanceof ExternInterfaceNode) {
			return TypeParsers.parseExternInterfaceReference(resolver, superCtx, superCtx.typeArguments(), (ExternInterfaceNode) superType, 0);
		} else {
			throw new AssertionError();
		}
	}
}
