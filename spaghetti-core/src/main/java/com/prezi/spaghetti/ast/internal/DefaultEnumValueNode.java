package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.EnumValueNode;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;

public class DefaultEnumValueNode extends AbstractNamedNode implements EnumValueNode, AnnotatedNodeInternal, DocumentedNodeInternal {
	private final NamedNodeSetInternal<AnnotationNode> annotations = NodeSets.newNamedNodeSet("annotation");
	private final Integer value;
	private DocumentationNode documentation = DocumentationNode.NONE;

	public DefaultEnumValueNode(Location location, String name, Integer value) {
		super(location, name);
		this.value = value;
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitEnumValueNode(this);
	}

	@Override
	public Integer getValue() {
		return value;
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
}
