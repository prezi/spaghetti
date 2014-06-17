package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.ExternNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.NamedNodeSet;

public class DefaultExternNode extends AbstractTypeNode implements ExternNode, MutableDocumentedNode {
	private final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<AnnotationNode>("annotation");
	private DocumentationNode documentation = DocumentationNode.NONE;

	public DefaultExternNode(FQName qualifiedName) {
		super(qualifiedName);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitExternNode(this);
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
