package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.InterfaceNode;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;

public class DefaultInterfaceReference extends AbstractParametrizedTypeNodeReference<InterfaceNode> implements InterfaceReferenceInternal {
	public DefaultInterfaceReference(Location location, InterfaceNode type, int arrayDimensions) {
		super(location, type, arrayDimensions);
	}

	@Override
	public TypeReferenceInternal withAdditionalArrayDimensions(int extraDimensions) {
		DefaultInterfaceReference node = new DefaultInterfaceReference(getLocation(), getType(), getArrayDimensions() + extraDimensions);
		node.getArgumentsInternal().addAll(getArgumentsInternal());
		return node;
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitInterfaceReference(this);
	}
}
