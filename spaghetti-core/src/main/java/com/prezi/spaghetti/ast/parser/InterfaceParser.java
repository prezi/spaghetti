package com.prezi.spaghetti.ast.parser;

import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.InterfaceNode;
import com.prezi.spaghetti.ast.InterfaceReference;
import com.prezi.spaghetti.ast.TypeNode;
import com.prezi.spaghetti.ast.internal.DefaultInterfaceMethodNode;
import com.prezi.spaghetti.ast.internal.DefaultInterfaceNode;
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

		for (ModuleParser.InterfaceMethodDefinitionContext methodCtx : getContext().interfaceMethodDefinition()) {
			TerminalNode nameCtx = methodCtx.methodDefinition().Name();
			DefaultInterfaceMethodNode methodNode = new DefaultInterfaceMethodNode(nameCtx.getText());
			AnnotationsParser.parseAnnotations(methodCtx.annotations(), methodNode);
			DocumentationParser.parseDocumentation(methodCtx.documentation, methodNode);
			MethodParser.parseMethodDefinition(resolver, methodCtx.methodDefinition(), methodNode);
			getNode().getMethods().add(methodNode, nameCtx);
		}

	}

	protected static InterfaceReference parseSuperInterface(TypeResolver resolver, ModuleParser.SuperInterfaceDefinitionContext superCtx) {
		TypeNode superType = resolver.resolveType(TypeResolutionContext.create(superCtx.qualifiedName()));
		if (!(superType instanceof InterfaceNode)) {
			throw new InternalAstParserException(superCtx, "Only interfaces can be super interfaces");
		}

		return TypeParsers.parseInterfaceReference(resolver, superCtx, superCtx.typeArguments(), (InterfaceNode) superType, 0);
	}

}
