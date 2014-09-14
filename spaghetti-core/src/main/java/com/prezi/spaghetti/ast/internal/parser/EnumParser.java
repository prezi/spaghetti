package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.EnumNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.internal.DefaultEnumNode;
import com.prezi.spaghetti.ast.internal.DefaultEnumValueNode;
import com.prezi.spaghetti.grammar.ModuleParser;

public class EnumParser extends AbstractModuleTypeParser<ModuleParser.EnumDefinitionContext, EnumNode> {
	public EnumParser(ModuleParser.EnumDefinitionContext context, String moduleName) {
		super(context, createNode(context, moduleName));
	}

	private static EnumNode createNode(ModuleParser.EnumDefinitionContext context, String moduleName) {
		DefaultEnumNode node = new DefaultEnumNode(FQName.fromString(moduleName, context.Name().getText()));
		AnnotationsParser.parseAnnotations(context.annotations(), node);
		DocumentationParser.parseDocumentation(context.documentation, node);
		return node;
	}

	@Override
	public void parse(TypeResolver resolver) {
		for (ModuleParser.EnumValueContext valueCtx : getContext().enumValue()) {
			DefaultEnumValueNode valueNode = new DefaultEnumValueNode(valueCtx.Name().getText());
			AnnotationsParser.parseAnnotations(valueCtx.annotations(), valueNode);
			DocumentationParser.parseDocumentation(valueCtx.documentation, valueNode);
			getNode().getValues().add(valueNode, valueCtx);
		}
	}
}
