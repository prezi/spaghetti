package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.StructNode;

public class DefaultStructReference extends AbstractParametrizedTypeNodeReference<StructNode> implements StructReferenceInternal {
	public DefaultStructReference(Location location, StructNode type, int arrayDimensions) {
		super(location, type, arrayDimensions);
	}

	@Override
	public TypeReferenceInternal withAdditionalArrayDimensions(int extraDimensions) {
		DefaultStructReference node = new DefaultStructReference(getLocation(), getType(), getArrayDimensions() + extraDimensions);
		node.getArgumentsInternal().addAll(getArgumentsInternal());
		return node;
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitStructReference(this);
	}
}
