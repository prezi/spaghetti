package com.prezi.spaghetti.ast.internal;

import com.google.common.base.Strings;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.PrimitiveType;
import com.prezi.spaghetti.ast.PrimitiveTypeReference;
import com.prezi.spaghetti.ast.TypeReference;

public class DefaultPrimitiveTypeReference extends AbstractTypeReference implements PrimitiveTypeReference {
	private final PrimitiveType type;

	public DefaultPrimitiveTypeReference(Location location, PrimitiveType type, int arrayDimensions) {
		super(location, arrayDimensions);
		this.type = type;
	}

	@Override
	public TypeReference withAdditionalArrayDimensions(int extraDimensions) {
		return new DefaultPrimitiveTypeReference(getLocation(), getType(), getArrayDimensions() + extraDimensions);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitPrimitiveTypeReference(this);
	}

	@Override
	public String toString() {
		return "&" + type.name().toLowerCase() + Strings.repeat("[]", getArrayDimensions());
	}

	@Override
	public final PrimitiveType getType() {
		return type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DefaultPrimitiveTypeReference that = (DefaultPrimitiveTypeReference) o;

		return type == that.type;
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}
}
