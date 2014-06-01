package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.internal.DefaultEnumNode
import com.prezi.spaghetti.ast.internal.DefaultEnumValueNode
import com.prezi.spaghetti.grammar.ModuleParser

/**
 * Created by lptr on 29/05/14.
 */
class EnumParser extends AbstractModuleTypeParser<ModuleParser.EnumDefinitionContext, EnumNode> {
	EnumParser(ModuleParser.EnumDefinitionContext context, String moduleName) {
		super(context, createNode(context, moduleName))
	}

	private static EnumNode createNode(ModuleParser.EnumDefinitionContext context, String moduleName) {
		def node = new DefaultEnumNode(FQName.fromString(moduleName, context.Name().text))
		AnnotationsParser.parseAnnotations(context.annotations(), node)
		DocumentationParser.parseDocumentation(context.documentation, node)
		return node
	}

	@Override
	void parse(TypeResolver resolver) {
		context.enumValue().each { ModuleParser.EnumValueContext valueCtx ->
			def valueNode = new DefaultEnumValueNode(valueCtx.Name().text)
			AnnotationsParser.parseAnnotations(valueCtx.annotations(), valueNode)
			DocumentationParser.parseDocumentation(valueCtx.documentation, valueNode)
			node.values.add valueNode, valueCtx
		}
	}
}
