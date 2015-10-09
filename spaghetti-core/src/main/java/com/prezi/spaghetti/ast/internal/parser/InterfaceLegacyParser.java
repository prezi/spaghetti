package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.ExternInterfaceNode;
import com.prezi.spaghetti.ast.InterfaceNode;
import com.prezi.spaghetti.ast.InterfaceNodeBase;
import com.prezi.spaghetti.ast.InterfaceReferenceBase;
import com.prezi.spaghetti.ast.TypeNode;
import com.prezi.spaghetti.ast.internal.DefaultFQName;
import com.prezi.spaghetti.ast.internal.DefaultInterfaceNode;
import com.prezi.spaghetti.ast.internal.DefaultMethodNode;
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterNode;
import com.prezi.spaghetti.internal.grammar.ModuleParser;
import org.antlr.v4.runtime.tree.TerminalNode;

public class InterfaceLegacyParser extends AbstractModuleTypeParser<ModuleParser.InterfaceDefinitionLegacyContext, DefaultInterfaceNode> {
	public InterfaceLegacyParser(Locator locator, ModuleParser.InterfaceDefinitionLegacyContext context, String moduleName) {
		super(locator, context, createNode(locator, context, moduleName));
	}

	private static DefaultInterfaceNode createNode(Locator locator, ModuleParser.InterfaceDefinitionLegacyContext context, String moduleName) {
		DefaultInterfaceNode node = new DefaultInterfaceNode(locator.locate(context.Name()), DefaultFQName.fromString(moduleName, context.Name().getText()));
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

		for (ModuleParser.SuperTypeDefinitionLegacyContext superCtx : getContext().superTypeDefinitionLegacy()) {
			node.getSuperInterfaces().addInternal(parseSuperType(locator, resolver, superCtx));
		}

		for (ModuleParser.MethodDefinitionLegacyContext methodCtx : getContext().methodDefinitionLegacy()) {
			DefaultMethodNode methodNode = MethodLegacyParser.parseMethodDefinition(locator, resolver, methodCtx);
			node.getMethods().add(methodNode, methodCtx.Name());
		}
	}

	private InterfaceReferenceBase<? extends InterfaceNodeBase> parseSuperType(Locator locator, TypeResolver resolver, ModuleParser.SuperTypeDefinitionLegacyContext superCtx) {
		TypeNode superType = resolver.resolveType(TypeResolutionContext.create(superCtx.qualifiedName()));
		if (!(superType instanceof InterfaceNodeBase)) {
			throw new InternalAstParserException(superCtx, "Only interfaces can be super interfaces");
		}

		if (superType instanceof InterfaceNode) {
			return TypeParsers.parseInterfaceReference(locator, resolver, superCtx, superCtx.typeArgumentsLegacy(), (InterfaceNode) superType, 0);
		} else if (superType instanceof ExternInterfaceNode) {
			return TypeParsers.parseExternInterfaceReference(locator, resolver, superCtx, superCtx.typeArgumentsLegacy(), (ExternInterfaceNode) superType, 0);
		} else {
			throw new AssertionError();
		}
	}
}
