package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.TypeParameterNode;

public abstract class AbstractParametrizedTypeNode extends AbstractTypeNode implements ParametrizedReferableTypeNodeInternal {
	private final NamedNodeSetInternal<TypeParameterNode> typeParameters = NodeSets.newNamedNodeSet("type parameter");

	public AbstractParametrizedTypeNode(Location location, FQName qualifiedName) {
		super(location, qualifiedName);
	}

	@Override
	public NamedNodeSetInternal<TypeParameterNode> getTypeParameters() {
		return typeParameters;
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), typeParameters);
	}
}
