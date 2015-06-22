package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.internal.DefaultExternInterfaceNode;
import com.prezi.spaghetti.ast.internal.DefaultFQName;
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterNode;
import com.prezi.spaghetti.internal.grammar.ModuleParser;
import org.antlr.v4.runtime.tree.TerminalNode;

public class ExternInterfaceParser extends AbstractModuleTypeParser<ModuleParser.ExternInterfaceDefinitionContext, DefaultExternInterfaceNode> {
	public ExternInterfaceParser(Locator locator, ModuleParser.ExternInterfaceDefinitionContext context) {
		super(locator, context, createNode(locator, context));
	}

	private static DefaultExternInterfaceNode createNode(Locator locator, ModuleParser.ExternInterfaceDefinitionContext context) {
		DefaultExternInterfaceNode node = new DefaultExternInterfaceNode(locator.locate(context.qualifiedName()), DefaultFQName.fromContext(context.qualifiedName()));
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
	}
}
