package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.TypeParameterNode;

public class DefaultTypeParameterReference extends AbstractTypeNodeReference<TypeParameterNode> implements TypeParameterReferenceInternal {
	public DefaultTypeParameterReference(Location location, TypeParameterNode type, int arrayDimensions) {
		super(location, type, arrayDimensions);
	}

	@Override
	public TypeReferenceInternal withAdditionalArrayDimensions(int extraDimensions) {
		return new DefaultTypeParameterReference(getLocation(), getType(), getArrayDimensions() + extraDimensions);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitTypeParameterReference(this);
	}
}
