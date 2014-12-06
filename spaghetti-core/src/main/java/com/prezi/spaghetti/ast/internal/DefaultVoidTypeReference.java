package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.TypeReference;
import com.prezi.spaghetti.ast.VoidTypeReference;

public class DefaultVoidTypeReference extends AbstractTypeReference implements VoidTypeReference {
	public DefaultVoidTypeReference(Location location, int arrayDimensions) {
		super(location, arrayDimensions);
	}

	@Override
	public TypeReference withAdditionalArrayDimensions(int extraDimensions) {
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
