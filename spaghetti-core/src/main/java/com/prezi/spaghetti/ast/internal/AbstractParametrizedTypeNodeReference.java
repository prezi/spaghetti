package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.ParametrizedReferableTypeNode;
import com.prezi.spaghetti.ast.ParametrizedTypeNodeReference;
import com.prezi.spaghetti.ast.TypeReference;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParametrizedTypeNodeReference<T extends ParametrizedReferableTypeNode> extends AbstractTypeNodeReference<T> implements ParametrizedTypeNodeReference<T> {
	private final List<TypeReference> arguments = new ArrayList<TypeReference>();

	public AbstractParametrizedTypeNodeReference(T type, int arrayDimensions) {
		super(type, arrayDimensions);
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), arguments);
	}

	@Override
	public List<TypeReference> getArguments() {
		return arguments;
	}
}
