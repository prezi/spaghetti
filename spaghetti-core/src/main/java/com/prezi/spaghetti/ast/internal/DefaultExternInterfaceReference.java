package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.ExternInterfaceNode;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;

public class DefaultExternInterfaceReference extends AbstractParametrizedTypeNodeReference<ExternInterfaceNode> implements ExternInterfaceReferenceInternal {
	public DefaultExternInterfaceReference(Location location, ExternInterfaceNode type, int arrayDimensions) {
		super(location, type, arrayDimensions);
	}

	@Override
	public TypeReferenceInternal withAdditionalArrayDimensions(int extraDimensions) {
		DefaultExternInterfaceReference node = new DefaultExternInterfaceReference(getLocation(), getType(), getArrayDimensions() + extraDimensions);
		node.getArgumentsInternal().addAll(getArgumentsInternal());
		return node;
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitExternInterfaceReference(this);
	}
}
