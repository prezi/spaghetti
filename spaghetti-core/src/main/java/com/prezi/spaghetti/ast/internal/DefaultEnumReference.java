package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.EnumNode;
import com.prezi.spaghetti.ast.EnumReference;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.TypeReference;

public class DefaultEnumReference extends AbstractTypeNodeReference<EnumNode> implements EnumReference {
	public DefaultEnumReference(Location location, EnumNode type, int arrayDimensions) {
		super(location, type, arrayDimensions);
	}

	@Override
	public TypeReference withAdditionalArrayDimensions(int extraDimensions) {
		return new DefaultEnumReference(getLocation(), getType(), getArrayDimensions() + extraDimensions);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitEnumReference(this);
	}
}
