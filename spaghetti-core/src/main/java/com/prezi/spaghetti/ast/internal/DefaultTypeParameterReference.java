package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.TypeParameterNode;
import com.prezi.spaghetti.ast.TypeParameterReference;
import com.prezi.spaghetti.ast.TypeReference;

public class DefaultTypeParameterReference extends AbstractTypeNodeReference<TypeParameterNode> implements TypeParameterReference {
	public DefaultTypeParameterReference(TypeParameterNode type, int arrayDimensions) {
		super(type, arrayDimensions);
	}

	@Override
	public TypeReference withAdditionalArrayDimensions(int extraDimensions) {
		return new DefaultTypeParameterReference(getType(), getArrayDimensions() + extraDimensions);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitTypeParameterReference(this);
	}
}
