package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.ArrayedTypeReference;

public abstract class AbstractArrayedTypeReference extends AbstractNode implements ArrayedTypeReference {

	private final int arrayDimensions;

	public AbstractArrayedTypeReference(int arrayDimensions) {
		this.arrayDimensions = arrayDimensions;
	}

	@Override
	public final int getArrayDimensions() {
		return arrayDimensions;
	}
}
