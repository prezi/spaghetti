package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.InterfaceNode;
import com.prezi.spaghetti.ast.InterfaceReference;
import com.prezi.spaghetti.ast.ModuleVisitor;

public class DefaultInterfaceReference extends AbstractParametrizedTypeNodeReference<InterfaceNode> implements InterfaceReference {
	public DefaultInterfaceReference(InterfaceNode type, int arrayDimensions) {
		super(type, arrayDimensions);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitInterfaceReference(this);
	}
}
