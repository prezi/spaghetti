package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.PrimitiveTypeReference;
import com.prezi.spaghetti.ast.internal.DefaultConstEntryNode;
import com.prezi.spaghetti.ast.internal.DefaultConstNode;
import com.prezi.spaghetti.ast.internal.DefaultFQName;
import com.prezi.spaghetti.ast.internal.PrimitiveTypeReferenceInternal;
import com.prezi.spaghetti.internal.grammar.ModuleParser;

public class ConstLegacyParser extends AbstractModuleTypeParser<ModuleParser.ConstDefinitionLegacyContext, DefaultConstNode> {
	public ConstLegacyParser(Locator locator, ModuleParser.ConstDefinitionLegacyContext context, String moduleName) {
		super(locator, context, createNode(locator, context, moduleName));
	}

	private static DefaultConstNode createNode(Locator locator, ModuleParser.ConstDefinitionLegacyContext context, String moduleName) {
		DefaultConstNode node = new DefaultConstNode(locator.locate(context.Name()), DefaultFQName.fromString(moduleName, context.Name().getText()));
		AnnotationsParser.parseAnnotations(locator, context.annotations(), node);
		DocumentationParser.parseDocumentation(locator, context.documentation, node);
		return node;
	}

	@Override
	public void parseInternal(TypeResolver resolver) {
		for (ModuleParser.ConstEntryLegacyContext entryCtx : getContext().constEntryLegacy()) {
			ModuleParser.ConstEntryDeclLegacyContext entryDeclCtx = entryCtx.constEntryDeclLegacy();

			String name = entryDeclCtx.Name().getText();
			PrimitiveTypeReference type;
			Object value;

			if (entryDeclCtx.Boolean() != null) {
				type = PrimitiveTypeReferenceInternal.BOOL;
				value = Primitives.parseBoolean(entryDeclCtx.Boolean().getSymbol());
			} else if (entryDeclCtx.Integer() != null) {
				type = PrimitiveTypeReferenceInternal.INT;
				value = Primitives.parseInt(entryDeclCtx.Integer().getSymbol());
			} else if (entryDeclCtx.Float() != null) {
				type = PrimitiveTypeReferenceInternal.FLOAT;
				value = Primitives.parseDouble(entryDeclCtx.Float().getSymbol());
			} else if (entryDeclCtx.String() != null) {
				type = PrimitiveTypeReferenceInternal.STRING;
				value = Primitives.parseString(entryDeclCtx.String().getSymbol());
			} else {
				throw new InternalAstParserException(entryDeclCtx, "Unknown primitive type");
			}

			DefaultConstEntryNode entry = new DefaultConstEntryNode(locator.locate(entryDeclCtx.Name()), name, type, value);
			AnnotationsParser.parseAnnotations(locator, entryCtx.annotations(), entry);
			DocumentationParser.parseDocumentation(locator, entryCtx.documentation, entry);
			node.getEntries().add(entry, entryCtx);
		}
	}
}
