package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ParametrizedReferableTypeNode;
import com.prezi.spaghetti.ast.ParametrizedTypeNodeReferenceInternal;
import com.prezi.spaghetti.ast.TypeReference;

import java.util.Collections;
import java.util.List;

public abstract class AbstractParametrizedTypeNodeReference<T extends ParametrizedReferableTypeNode> extends AbstractTypeNodeReference<T> implements ParametrizedTypeNodeReferenceInternal<T> {
	private final List<TypeReference> argumentsInternal = Lists.newArrayList();
	private final List<TypeReference> arguments = Collections.unmodifiableList(argumentsInternal);

	public AbstractParametrizedTypeNodeReference(Location location, T type, int arrayDimensions) {
		super(location, type, arrayDimensions);
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), argumentsInternal);
	}

	@Override
	public List<TypeReference> getArguments() {
		return arguments;
	}

	@Override
	public List<TypeReference> getArgumentsInternal() {
		return argumentsInternal;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractParametrizedTypeNodeReference)) return false;
		if (!super.equals(o)) return false;

		AbstractParametrizedTypeNodeReference that = (AbstractParametrizedTypeNodeReference) o;

		if (!argumentsInternal.equals(that.argumentsInternal)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + argumentsInternal.hashCode();
		return result;
	}
}
