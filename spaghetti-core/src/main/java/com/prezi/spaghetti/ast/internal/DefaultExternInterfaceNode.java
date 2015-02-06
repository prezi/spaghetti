package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.ExternInterfaceNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;

public class DefaultExternInterfaceNode extends AbstractParametrizedTypeNode implements ExternInterfaceNode, AnnotatedNodeInternal, DocumentedNodeInternal {
	private final NamedNodeSetInternal<AnnotationNode> annotations = NodeSets.newNamedNodeSet("annotation");
	private DocumentationNode documentation = DocumentationNode.NONE;

	public DefaultExternInterfaceNode(Location location, FQName qualifiedName) {
		super(location, qualifiedName);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitExternInterfaceNode(this);
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
