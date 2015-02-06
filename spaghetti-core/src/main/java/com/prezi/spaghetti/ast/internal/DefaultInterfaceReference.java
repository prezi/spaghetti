package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.InterfaceNode;
import com.prezi.spaghetti.ast.InterfaceReference;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.TypeReference;

public class DefaultInterfaceReference extends AbstractParametrizedTypeNodeReference<InterfaceNode> implements InterfaceReference {
	public DefaultInterfaceReference(Location location, InterfaceNode type, int arrayDimensions) {
		super(location, type, arrayDimensions);
	}

	@Override
	public TypeReference withAdditionalArrayDimensions(int extraDimensions) {
		DefaultInterfaceReference node = new DefaultInterfaceReference(getLocation(), getType(), getArrayDimensions() + extraDimensions);
		node.getArgumentsInternal().addAll(getArguments());
		return node;
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitInterfaceReference(this);
	}
}
