package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.ConstNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.PrimitiveTypeReference;
import com.prezi.spaghetti.ast.internal.DefaultConstEntryNode;
import com.prezi.spaghetti.ast.internal.DefaultConstNode;
import com.prezi.spaghetti.internal.grammar.ModuleParser;

public class ConstParser extends AbstractModuleTypeParser<ModuleParser.ConstDefinitionContext, ConstNode> {
	public ConstParser(ModuleParser.ConstDefinitionContext context, String moduleName) {
		super(context, createNode(context, moduleName));
	}

	private static ConstNode createNode(ModuleParser.ConstDefinitionContext context, String moduleName) {
		DefaultConstNode node = new DefaultConstNode(FQName.fromString(moduleName, context.Name().getText()));
		AnnotationsParser.parseAnnotations(context.annotations(), node);
		DocumentationParser.parseDocumentation(context.documentation, node);
		return node;
	}

	@Override
	public void parse(TypeResolver resolver) {
		for (ModuleParser.ConstEntryContext entryCtx : getContext().constEntry()) {
			ModuleParser.ConstEntryDeclContext entryDeclCtx = entryCtx.constEntryDecl();

			String name = entryDeclCtx.Name().getText();
			PrimitiveTypeReference type;
			Object value;

			if (entryDeclCtx.Boolean() != null) {
				type = PrimitiveTypeReference.BOOL;
				value = Primitives.parseBoolean(entryDeclCtx.Boolean().getSymbol());
			} else if (entryDeclCtx.Integer() != null) {
				type = PrimitiveTypeReference.INT;
				value = Primitives.parseInt(entryDeclCtx.Integer().getSymbol());
			} else if (entryDeclCtx.Float() != null) {
				type = PrimitiveTypeReference.FLOAT;
				value = Primitives.parseDouble(entryDeclCtx.Float().getSymbol());
			} else if (entryDeclCtx.String() != null) {
				type = PrimitiveTypeReference.STRING;
				value = Primitives.parseString(entryDeclCtx.String().getSymbol());
			} else {
				throw new InternalAstParserException(entryDeclCtx, "Unknown primitive type");
			}

			DefaultConstEntryNode entry = new DefaultConstEntryNode(name, type, value);
			AnnotationsParser.parseAnnotations(entryCtx.annotations(), entry);
			DocumentationParser.parseDocumentation(entryCtx.documentation, entry);
			getNode().getEntries().add(entry, entryCtx);
		}
	}
}
