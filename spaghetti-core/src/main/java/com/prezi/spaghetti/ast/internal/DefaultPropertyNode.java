package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.NamedNodeSet;
import com.prezi.spaghetti.ast.NodeSets;
import com.prezi.spaghetti.ast.PropertyNode;
import com.prezi.spaghetti.ast.TypeReference;

public class DefaultPropertyNode extends AbstractTypeNamePairNode<TypeReference> implements PropertyNode, MutableDocumentedNode {
	private final NamedNodeSet<AnnotationNode> annotations = NodeSets.newNamedNodeSet("annotation");
	private DocumentationNode documentation = DocumentationNode.NONE;
	private final boolean optional;

	public DefaultPropertyNode(String name, TypeReference type, boolean optional) {
		super(name, type);
		this.optional = optional;
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitPropertyNode(this);
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

	@Override
	public NamedNodeSet<AnnotationNode> getAnnotations() {
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
