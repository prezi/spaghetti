package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.MethodParameterNode;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.NamedNodeSet;
import com.prezi.spaghetti.ast.TypeReference;

public class DefaultMethodParameterNode extends AbstractTypeNamePairNode<TypeReference> implements MethodParameterNode {

	private final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<AnnotationNode>("annotation");
	private final boolean isOptional;
	private final Object optionalValue;

	public DefaultMethodParameterNode(String name, TypeReference type, boolean isOptional, Object optionalValue) {
		super(name, type);
		this.isOptional = isOptional;
		this.optionalValue = optionalValue;
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

	@Override
	public Object getOptionalValue() {
		return optionalValue;
	}
}
