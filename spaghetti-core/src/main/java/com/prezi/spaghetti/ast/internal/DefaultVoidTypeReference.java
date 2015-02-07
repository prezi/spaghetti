package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;

public class DefaultVoidTypeReference extends AbstractTypeReference implements VoidTypeReferenceInternal {
	public DefaultVoidTypeReference(Location location, int arrayDimensions) {
		super(location, arrayDimensions);
	}

	@Override
	public TypeReferenceInternal withAdditionalArrayDimensions(int extraDimensions) {
		return new DefaultVoidTypeReference(getLocation(), getArrayDimensions() + extraDimensions);
	}

	@Override
	public <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitVoidTypeReference(this);
	}

	@Override
	public String toString() {
		return "void";
	}
}
