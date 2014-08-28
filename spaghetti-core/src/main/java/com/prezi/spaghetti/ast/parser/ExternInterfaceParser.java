package com.prezi.spaghetti.ast.parser;

import com.prezi.spaghetti.ast.ExternInterfaceNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.internal.DefaultExternInterfaceNode;
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterNode;
import com.prezi.spaghetti.grammar.ModuleParser;
import org.antlr.v4.runtime.tree.TerminalNode;

public class ExternInterfaceParser extends AbstractModuleTypeParser<ModuleParser.ExternInterfaceDefinitionContext, ExternInterfaceNode> {
	public ExternInterfaceParser(ModuleParser.ExternInterfaceDefinitionContext context) {
		super(context, createNode(context));
	}

	private static ExternInterfaceNode createNode(ModuleParser.ExternInterfaceDefinitionContext context) {
		DefaultExternInterfaceNode node = new DefaultExternInterfaceNode(FQName.fromContext(context.qualifiedName()));
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
	}
}
