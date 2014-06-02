package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.ast.internal.DefaultPropertyNode
import com.prezi.spaghetti.ast.internal.DefaultStructNode
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterNode
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * Created by lptr on 29/05/14.
 */
class StructParser extends AbstractModuleTypeParser<ModuleParser.StructDefinitionContext, StructNode> {
	StructParser(ModuleParser.StructDefinitionContext context, String moduleName) {
		super(context, createNode(context, moduleName))
	}

	private static StructNode createNode(ModuleParser.StructDefinitionContext context, String moduleName) {
		def node = new DefaultStructNode(FQName.fromString(moduleName, context.Name().text))
		AnnotationsParser.parseAnnotations(context.annotations(), node)
		DocumentationParser.parseDocumentation(context.documentation, node)
		context.typeParameters()?.Name()?.each { TerminalNode name ->
			node.typeParameters.add new DefaultTypeParameterNode(name.text), context
		}
		return node
	}

	@Override
	void parse(TypeResolver resolver) {
		// Let further processing access type parameters as defined types
		resolver = new SimpleNamedTypeResolver(resolver, node.typeParameters)

		context.propertyDefinition().each { ModuleParser.PropertyDefinitionContext propCtx ->
			def name = propCtx.typeNamePair().Name().text
			def type = TypeParsers.parseComplexType(resolver, propCtx.typeNamePair().complexType())
			def propertyNode = new DefaultPropertyNode(name, type)
			AnnotationsParser.parseAnnotations(propCtx.annotations(), propertyNode)
			DocumentationParser.parseDocumentation(propCtx.documentation, propertyNode)
			node.properties.add propertyNode, propCtx
		}
	}
}
