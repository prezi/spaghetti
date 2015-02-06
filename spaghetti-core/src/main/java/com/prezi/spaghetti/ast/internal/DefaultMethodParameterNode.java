package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.MethodParameterNode;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.TypeParameterNode;
import com.prezi.spaghetti.ast.TypeReference;

import java.util.Map;

public class DefaultMethodParameterNode extends AbstractTypeNamePairNode<TypeReference> implements MethodParameterNode, AnnotatedNodeInternal {

	private final NamedNodeSetInternal<AnnotationNode> annotations = NodeSets.newNamedNodeSet("annotation");
	private final boolean isOptional;

	public DefaultMethodParameterNode(Location location, String name, TypeReference type, boolean isOptional) {
		super(location, name, type);
		this.isOptional = isOptional;
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitMethodParameterNode(this);
	}

	@Override
	public NamedNodeSetInternal<AnnotationNode> getAnnotations() {
		return annotations;
	}

	@Override
	public boolean isOptional() {
		return isOptional;
	}

	@Override
	public MethodParameterNode resolveWithTypeParameters(Map<TypeParameterNode, TypeReference> bindings) {
		TypeReference type = TypeParameterResolver.resolveTypeParameters(getType(), bindings);
		DefaultMethodParameterNode resolvedParam = new DefaultMethodParameterNode(getLocation(), getName(), type, isOptional());
		resolvedParam.getAnnotations().addAllInternal(getAnnotations());
		return resolvedParam;
	}
}
