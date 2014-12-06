package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.ExternInterfaceNode;
import com.prezi.spaghetti.ast.ExternInterfaceReference;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.TypeReference;

public class DefaultExternInterfaceReference extends AbstractParametrizedTypeNodeReference<ExternInterfaceNode> implements ExternInterfaceReference {
	public DefaultExternInterfaceReference(Location location, ExternInterfaceNode type, int arrayDimensions) {
		super(location, type, arrayDimensions);
	}

	@Override
	public TypeReference withAdditionalArrayDimensions(int extraDimensions) {
		return new DefaultExternInterfaceReference(getLocation(), getType(), getArrayDimensions() + extraDimensions);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitExternInterfaceReference(this);
	}
}
