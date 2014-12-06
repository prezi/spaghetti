package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.StructNode;
import com.prezi.spaghetti.ast.StructReference;
import com.prezi.spaghetti.ast.TypeReference;

public class DefaultStructReference extends AbstractParametrizedTypeNodeReference<StructNode> implements StructReference {
	public DefaultStructReference(Location location, StructNode type, int arrayDimensions) {
		super(location, type, arrayDimensions);
	}

	@Override
	public TypeReference withAdditionalArrayDimensions(int extraDimensions) {
		return new DefaultStructReference(getLocation(), getType(), getArrayDimensions() + extraDimensions);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitStructReference(this);
	}
}
