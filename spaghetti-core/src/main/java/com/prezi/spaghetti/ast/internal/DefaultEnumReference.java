package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.EnumNode;
import com.prezi.spaghetti.ast.EnumReference;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.TypeReference;

public class DefaultEnumReference extends AbstractTypeNodeReference<EnumNode> implements EnumReference {
	public DefaultEnumReference(EnumNode type, int arrayDimensions) {
		super(type, arrayDimensions);
	}

	@Override
	public TypeReference withAdditionalArrayDimensions(int extraDimensions) {
		return new DefaultEnumReference(getType(), getArrayDimensions() + extraDimensions);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitEnumReference(this);
	}
}
