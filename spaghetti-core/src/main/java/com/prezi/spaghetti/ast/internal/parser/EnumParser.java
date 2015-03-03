package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.internal.DefaultEnumNode;
import com.prezi.spaghetti.ast.internal.DefaultEnumValueNode;
import com.prezi.spaghetti.internal.grammar.ModuleParser;

public class EnumParser extends AbstractModuleTypeParser<ModuleParser.EnumDefinitionContext, DefaultEnumNode> {
	public EnumParser(Locator locator, ModuleParser.EnumDefinitionContext context, String moduleName) {
		super(locator, context, createNode(locator, context, moduleName));
	}

	private static DefaultEnumNode createNode(Locator locator, ModuleParser.EnumDefinitionContext context, String moduleName) {
		DefaultEnumNode node = new DefaultEnumNode(locator.locate(context.Name()), FQName.fromString(moduleName, context.Name().getText()));
		AnnotationsParser.parseAnnotations(locator, context.annotations(), node);
		DocumentationParser.parseDocumentation(locator, context.documentation, node);
		return node;
	}

	@Override
	public void parseInternal(TypeResolver resolver) {
		for (ModuleParser.EnumValueContext valueCtx : getContext().enumValue()) {
			DefaultEnumValueNode valueNode = new DefaultEnumValueNode(locator.locate(valueCtx.Name()), valueCtx.Name().getText());
			AnnotationsParser.parseAnnotations(locator, valueCtx.annotations(), valueNode);
			DocumentationParser.parseDocumentation(locator, valueCtx.documentation, valueNode);
			node.getValues().add(valueNode, valueCtx);
		}
	}
}
