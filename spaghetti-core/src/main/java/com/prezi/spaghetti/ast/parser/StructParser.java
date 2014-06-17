package com.prezi.spaghetti.ast.parser;

import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.StructNode;
import com.prezi.spaghetti.ast.TypeReference;
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

		for (ModuleParser.PropertyDefinitionContext propCtx : getContext().propertyDefinition()) {
			String name = propCtx.typeNamePair().Name().getText();
			TypeReference type = TypeParsers.parseComplexType(resolver, propCtx.typeNamePair().complexType());
			DefaultPropertyNode propertyNode = new DefaultPropertyNode(name, type);
			AnnotationsParser.parseAnnotations(propCtx.annotations(), propertyNode);
			DocumentationParser.parseDocumentation(propCtx.documentation, propertyNode);
			getNode().getProperties().add(propertyNode, propCtx);
		}
	}
}
