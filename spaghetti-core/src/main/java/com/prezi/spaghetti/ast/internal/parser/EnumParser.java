package com.prezi.spaghetti.ast.internal.parser;

import com.google.common.collect.Sets;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.internal.DefaultEnumNode;
import com.prezi.spaghetti.ast.internal.DefaultEnumValueNode;
import com.prezi.spaghetti.internal.grammar.ModuleParser;

import java.util.Set;

public class EnumParser extends AbstractModuleTypeParser<ModuleParser.EnumDefinitionContext, DefaultEnumNode> {

	private enum EnumAssignmentStyle {
		// Enums with no entries
		NoEntries,
		// Enums with one or more entries, all implicit
		Implicit,
		// Enums with one or more entries, all explicit
		Explicit,
		// Enums with at least one implicit and explicit assignment
		Mixed
	}

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
		EnumAssignmentStyle enumStyle = EnumAssignmentStyle.NoEntries;
		// Counter for implicitly numbered enum entries
		int ordinal = 0;
		// Tally set to detect duplicate entry numberings
		Set<Integer> seenValues = Sets.newLinkedHashSet();
		for (ModuleParser.EnumValueContext valueCtx : getContext().enumValue()) {
			// Gather enum value parameters
			Integer value = Primitives.parseInt(valueCtx.value);
			Location location = locator.locate(valueCtx.Name());
			String name = valueCtx.Name().getText();

			// Detect assignment style
			EnumAssignmentStyle entryStyle =
					value == null ? EnumAssignmentStyle.Implicit : EnumAssignmentStyle.Explicit;
			// The style of the first entry determines the enumStyle
			if (enumStyle == EnumAssignmentStyle.NoEntries) {
				enumStyle = entryStyle;
			// If any later entryStyle differs from the enumStyle, we have illegal mixed style
			} else if (enumStyle != entryStyle) {
				throw new AstParserException(
						location.getSource(),
						"Mixed implicit and explicit entries in enum " + node.getName());
			}

			// Normalize value for this entry
			// Explicate implicit values
			if (enumStyle == EnumAssignmentStyle.Implicit) {
				value = ordinal++;
			// Perform uniqueness check on explicit values
			} else if (enumStyle == EnumAssignmentStyle.Explicit) {
				if (!seenValues.add(value)) {
					throw new AstParserException(location.getSource(), "Duplicate value in enum " + node.getName());
				}
			}

			// Construct and add value node
			DefaultEnumValueNode valueNode = new DefaultEnumValueNode(location, name, value);
			AnnotationsParser.parseAnnotations(locator, valueCtx.annotations(), valueNode);
			DocumentationParser.parseDocumentation(locator, valueCtx.documentation, valueNode);
			node.getValues().add(valueNode, valueCtx);
		}
	}
}
