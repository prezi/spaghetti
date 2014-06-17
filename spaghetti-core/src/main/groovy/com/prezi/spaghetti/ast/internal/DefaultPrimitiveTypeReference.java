package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.PrimitiveType;
import com.prezi.spaghetti.ast.PrimitiveTypeReference;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

public class DefaultPrimitiveTypeReference extends AbstractArrayedTypeReference implements PrimitiveTypeReference {
	private final PrimitiveType type;

	public DefaultPrimitiveTypeReference(PrimitiveType type, int arrayDimensions) {
		super(arrayDimensions);
		this.type = type;
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitPrimitiveTypeReference(this);
	}

	@Override
	public String toString() {
		return "&" + type.name().toLowerCase() + DefaultGroovyMethods.multiply("[]", getArrayDimensions());
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
