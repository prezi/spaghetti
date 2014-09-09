package com.prezi.spaghetti.ast.parser;

import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.StructNode;
import com.prezi.spaghetti.ast.TypeReference;
import com.prezi.spaghetti.ast.internal.DefaultMethodNode;
import com.prezi.spaghetti.ast.internal.DefaultPropertyNode;
import com.prezi.spaghetti.ast.internal.DefaultStructNode;
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterNode;
import com.prezi.spaghetti.grammar.ModuleParser;
import org.antlr.v4.runtime.tree.TerminalNode;

public class StructParser extends AbstractModuleTypeParser<ModuleParser.StructDefinitionContext, StructNode> {
	public StructParser(ModuleParser.StructDefinitionContext context, String moduleName) {
		super(context, createNode(context, moduleName));
	}

	private static StructNode createNode(ModuleParser.StructDefinitionContext context, String moduleName) {
		DefaultStructNode node = new DefaultStructNode(FQName.fromString(moduleName, context.Name().getText()));
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

		for (ModuleParser.StructElementDefinitionContext elemCtx : getContext().structElementDefinition()) {
			if (elemCtx.propertyDefinition() != null) {
				ModuleParser.PropertyDefinitionContext propCtx = elemCtx.propertyDefinition();
				ModuleParser.TypeNamePairContext pairCtx = propCtx.typeNamePair();
				String name = pairCtx.Name().getText();
				TypeReference type = TypeParsers.parseComplexType(resolver, pairCtx.complexType());
				boolean optional = propCtx.optional != null;

				DefaultPropertyNode propertyNode = new DefaultPropertyNode(name, type, optional);
				AnnotationsParser.parseAnnotations(propCtx.annotations(), propertyNode);
				DocumentationParser.parseDocumentation(propCtx.documentation, propertyNode);
				getNode().getProperties().add(propertyNode, propCtx);
			} else if (elemCtx.methodDefinition() != null) {
				ModuleParser.MethodDefinitionContext methodCtx = elemCtx.methodDefinition();
				DefaultMethodNode methodNode = MethodParser.parseMethodDefinition(resolver, methodCtx);
				getNode().getMethods().add(methodNode, methodCtx.Name());
			}
		}
	}
}
