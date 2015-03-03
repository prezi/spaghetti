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
import com.prezi.spaghetti.internal.grammar.ModuleParser;
import org.antlr.v4.runtime.tree.TerminalNode;

public class InterfaceParser extends AbstractModuleTypeParser<ModuleParser.InterfaceDefinitionContext, DefaultInterfaceNode> {
	public InterfaceParser(Locator locator, ModuleParser.InterfaceDefinitionContext context, String moduleName) {
		super(locator, context, createNode(locator, context, moduleName));
	}

	private static DefaultInterfaceNode createNode(Locator locator, ModuleParser.InterfaceDefinitionContext context, String moduleName) {
		DefaultInterfaceNode node = new DefaultInterfaceNode(locator.locate(context.Name()), FQName.fromString(moduleName, context.Name().getText()));
		AnnotationsParser.parseAnnotations(locator, context.annotations(), node);
		DocumentationParser.parseDocumentation(locator, context.documentation, node);

		ModuleParser.TypeParametersContext typeParameters = context.typeParameters();
		if (typeParameters != null) {
			for (TerminalNode name : typeParameters.Name()) {
				node.getTypeParameters().add(new DefaultTypeParameterNode(locator.locate(name), name.getText()), context);
			}
		}

		return node;
	}

	@Override
	public void parseInternal(TypeResolver resolver) {
		// Let further processing access type parameters as defined types
		resolver = new SimpleNamedTypeResolver(resolver, node.getTypeParameters());

		for (ModuleParser.SuperTypeDefinitionContext superCtx : getContext().superTypeDefinition()) {
			node.getSuperInterfaces().addInternal(parseSuperType(locator, resolver, superCtx));
		}

		for (ModuleParser.MethodDefinitionContext methodCtx : getContext().methodDefinition()) {
			DefaultMethodNode methodNode = MethodParser.parseMethodDefinition(locator, resolver, methodCtx);
			node.getMethods().add(methodNode, methodCtx.Name());
		}
	}

	private InterfaceReferenceBase<? extends InterfaceNodeBase> parseSuperType(Locator locator, TypeResolver resolver, ModuleParser.SuperTypeDefinitionContext superCtx) {
		TypeNode superType = resolver.resolveType(TypeResolutionContext.create(superCtx.qualifiedName()));
		if (!(superType instanceof InterfaceNodeBase)) {
			throw new InternalAstParserException(superCtx, "Only interfaces can be super interfaces");
		}

		if (superType instanceof InterfaceNode) {
			return TypeParsers.parseInterfaceReference(locator, resolver, superCtx, superCtx.typeArguments(), (InterfaceNode) superType, 0);
		} else if (superType instanceof ExternInterfaceNode) {
			return TypeParsers.parseExternInterfaceReference(locator, resolver, superCtx, superCtx.typeArguments(), (ExternInterfaceNode) superType, 0);
		} else {
			throw new AssertionError();
		}
	}
}
