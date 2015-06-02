package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.EnumNode;
import com.prezi.spaghetti.ast.EnumValueNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.internal.parser.AstParserException;

import java.util.Set;

public class DefaultEnumNode extends AbstractTypeNode implements EnumNode, AnnotatedNodeInternal, DocumentedNodeInternal {

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

	private final NamedNodeSetInternal<AnnotationNode> annotations = NodeSets.newNamedNodeSet("annotation");
	private DocumentationNode documentation = DocumentationNode.NONE;
	private final NamedNodeSetInternal<EnumValueNode> values = makeEmptyEnumValueNodeSet();

	public DefaultEnumNode(Location location, FQName qualifiedName) {
		super(location, qualifiedName);
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), values);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitEnumNode(this);
	}

	@Override
	public NamedNodeSetInternal<AnnotationNode> getAnnotations() {
		return annotations;
	}

	@Override
	public DocumentationNode getDocumentation() {
		return documentation;
	}

	@Override
	public void setDocumentation(DocumentationNode documentation) {
		this.documentation = documentation;
	}

	@Override
	public NamedNodeSetInternal<EnumValueNode> getValues() {
		return values;
	}

	@Override
	public NamedNodeSetInternal<EnumValueNode> getNormalizedValues() {
		NamedNodeSetInternal<EnumValueNode> nodes = makeEmptyEnumValueNodeSet();
		switch (this.detectAssignmentStyle()) {
			case NoEntries:
				break;
			case Implicit:
				// Enum entry values are determined from order of appearance
				int ordinal = 0;
				for (EnumValueNode enumValueNode : values) {
					nodes.addInternal(makeValueNodeFrom(enumValueNode, ordinal++));
				}
				break;
			case Explicit:
				// Enum entry values are explicitly given
				Set<Integer> seenValues = Sets.newLinkedHashSet();
				for (EnumValueNode enumValueNode : values) {
					Integer value = enumValueNode.getValue();
					if (!seenValues.add(value)) {
						throw new AstParserException(
								this.getLocation().getSource(),
								"Duplicate value in enum " + this.getName());
					}
					nodes.addInternal(makeValueNodeFrom(enumValueNode, value));
				}
				break;
			case Mixed:
				throw new AstParserException(
						this.getLocation().getSource(),
						"Mixed implicit and explicit entries in enum " + this.getName());
		}
		return nodes;
	}

	private EnumAssignmentStyle detectAssignmentStyle() {
		EnumAssignmentStyle enumStyle = EnumAssignmentStyle.NoEntries;
		for (EnumValueNode enumValueNode : values) {
			EnumAssignmentStyle entryStyle =
					enumValueNode.getValue() == null ? EnumAssignmentStyle.Implicit : EnumAssignmentStyle.Explicit;
			// The first entryStyle determines the enumStyle
			if (enumStyle == EnumAssignmentStyle.NoEntries) {
				enumStyle = entryStyle;
			// If any later entryStyle differs from the enumStyle, we have mixed style
			} else if (enumStyle != entryStyle) {
				return EnumAssignmentStyle.Mixed;
			}
		}
		return enumStyle;
	}

	private static EnumValueNode makeValueNodeFrom(EnumValueNode valueNode, Integer value) {
		DefaultEnumValueNode newNode = new DefaultEnumValueNode(valueNode.getLocation(), valueNode.getName(), value);
		newNode.getAnnotations().addAllInternal(valueNode.getAnnotations());
		newNode.setDocumentation(valueNode.getDocumentation());
		return newNode;
	}

	private static NamedNodeSetInternal<EnumValueNode> makeEmptyEnumValueNodeSet() {
		return NodeSets.newNamedNodeSet("enum value");
	}
}
