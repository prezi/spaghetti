package com.prezi.spaghetti.ast.internal;

import com.google.common.base.Strings;
import com.prezi.spaghetti.ast.ReferableTypeNode;
import com.prezi.spaghetti.ast.TypeNodeReference;

public abstract class AbstractTypeNodeReference<T extends ReferableTypeNode> extends AbstractTypeReference implements TypeNodeReference<T> {
	private final T type;

	public AbstractTypeNodeReference(T type, int arrayDimensions) {
		super(arrayDimensions);
		this.type = type;
	}

	@Override
	public String toString() {
		return "%" + type + Strings.repeat("[]", getArrayDimensions());
	}

	@Override
	public final T getType() {
		return type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractTypeNodeReference)) return false;
		if (!super.equals(o)) return false;

		AbstractTypeNodeReference that = (AbstractTypeNodeReference) o;

		if (!type.equals(that.type)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + type.hashCode();
		return result;
	}
}
