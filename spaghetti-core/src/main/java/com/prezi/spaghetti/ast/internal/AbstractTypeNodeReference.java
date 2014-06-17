package com.prezi.spaghetti.ast.internal;

import com.google.common.base.Strings;
import com.prezi.spaghetti.ast.ReferableTypeNode;
import com.prezi.spaghetti.ast.TypeNodeReference;

public abstract class AbstractTypeNodeReference<T extends ReferableTypeNode> extends AbstractArrayedTypeReference implements TypeNodeReference<T> {
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
}
