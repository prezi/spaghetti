package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.PrimitiveTypeReference;

public class DefaultConstEntryNode extends AbstractTypeNamePairNode<PrimitiveTypeReference> implements ConstEntryNodeInternal {
	private final NamedNodeSetInternal<AnnotationNode> annotations = NodeSets.newNamedNodeSet("annotation");
	private DocumentationNode documentation = DocumentationNodeInternal.NONE;
	private final Object value;

	public DefaultConstEntryNode(Location location, String name, PrimitiveTypeReference type, Object value) {
		super(location, name, type);
		this.value = value;
	}

	@Override
	public <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitConstEntryNode(this);
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
	public final Object getValue() {
		return value;
	}
}
