package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.Location;

public abstract class AbstractTypeReference extends AbstractNode implements TypeReferenceInternal {

	private final int arrayDimensions;

	public AbstractTypeReference(Location location, int arrayDimensions) {
		super(location);
		this.arrayDimensions = arrayDimensions;
	}

	@Override
	public final int getArrayDimensions() {
		return arrayDimensions;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractTypeReference)) return false;

		AbstractTypeReference that = (AbstractTypeReference) o;

		if (arrayDimensions != that.arrayDimensions) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return arrayDimensions;
	}
}
