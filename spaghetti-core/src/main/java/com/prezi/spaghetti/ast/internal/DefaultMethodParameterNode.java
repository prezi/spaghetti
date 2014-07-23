package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.MethodParameterNode;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.NamedNodeSet;
import com.prezi.spaghetti.ast.TypeReference;

public class DefaultMethodParameterNode extends AbstractTypeNamePairNode<TypeReference> implements MethodParameterNode {

	private final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<AnnotationNode>("annotation");
	private final boolean isOptional;

	public DefaultMethodParameterNode(String name, TypeReference type, boolean isOptional) {
		super(name, type);
		this.isOptional = isOptional;
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitMethodParameterNode(this);
	}

	@Override
	public NamedNodeSet<AnnotationNode> getAnnotations() {
		return annotations;
	}

	@Override
	public boolean isOptional() {
		return isOptional;
	}
}
