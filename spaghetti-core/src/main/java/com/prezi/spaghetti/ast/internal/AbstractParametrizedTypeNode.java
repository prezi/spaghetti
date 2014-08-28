package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.NamedNodeSet;
import com.prezi.spaghetti.ast.ParametrizedReferableTypeNode;
import com.prezi.spaghetti.ast.TypeParameterNode;

public abstract class AbstractParametrizedTypeNode extends AbstractTypeNode implements ParametrizedReferableTypeNode {
	private final NamedNodeSet<TypeParameterNode> typeParameters = new DefaultNamedNodeSet<TypeParameterNode>("type parameter");

	public AbstractParametrizedTypeNode(FQName qualifiedName) {
		super(qualifiedName);
	}

	@Override
	public NamedNodeSet<TypeParameterNode> getTypeParameters() {
		return typeParameters;
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), typeParameters);
	}
}
