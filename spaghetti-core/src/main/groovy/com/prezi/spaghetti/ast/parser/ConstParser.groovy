package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.PrimitiveTypeReference
import com.prezi.spaghetti.ast.internal.DefaultConstEntryNode
import com.prezi.spaghetti.ast.internal.DefaultConstNode
import com.prezi.spaghetti.grammar.ModuleParser

class ConstParser extends AbstractModuleTypeParser<ModuleParser.ConstDefinitionContext, ConstNode> {
	ConstParser(ModuleParser.ConstDefinitionContext context, String moduleName) {
		super(context, createNode(context, moduleName))
	}

	private static ConstNode createNode(ModuleParser.ConstDefinitionContext context, String moduleName) {
		def node = new DefaultConstNode(FQName.fromString(moduleName, context.Name().text))
		AnnotationsParser.parseAnnotations(context.annotations(), node)
		DocumentationParser.parseDocumentation(context.documentation, node)
		return node
	}

	@Override
	void parse(TypeResolver resolver) {
		context.constEntry().each { ModuleParser.ConstEntryContext entryCtx ->
			def entryDeclCtx = entryCtx.constEntryDecl()

			def name = entryDeclCtx.Name().text
			PrimitiveTypeReference type
			Object value

			if (entryDeclCtx.Boolean()) {
				type = PrimitiveTypeReference.BOOL
				value = Primitives.parseBoolean(entryDeclCtx.Boolean().symbol)
			} else if (entryDeclCtx.Integer()) {
				type = PrimitiveTypeReference.INT
				value = Primitives.parseInt(entryDeclCtx.Integer().symbol)
			} else if (entryDeclCtx.Float()) {
				type = PrimitiveTypeReference.FLOAT
				value = Primitives.parseDouble(entryDeclCtx.Float().symbol)
			} else if (entryDeclCtx.String()) {
				type = PrimitiveTypeReference.STRING
				value = Primitives.parseString(entryDeclCtx.String().symbol)
			} else {
				throw new InternalAstParserException(entryDeclCtx, "Unknown primitive type")
			}
			def entry = new DefaultConstEntryNode(name, type, value)
			AnnotationsParser.parseAnnotations(entryCtx.annotations(), entry)
			DocumentationParser.parseDocumentation(entryCtx.documentation, entry)
			node.entries.add entry, entryCtx
		}
	}
}
