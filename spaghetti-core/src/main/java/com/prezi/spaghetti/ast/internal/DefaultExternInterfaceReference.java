package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.ExternInterfaceNode;
import com.prezi.spaghetti.ast.ExternInterfaceReference;
import com.prezi.spaghetti.ast.ModuleVisitor;

public class DefaultExternInterfaceReference extends AbstractParametrizedTypeNodeReference<ExternInterfaceNode> implements ExternInterfaceReference {
	public DefaultExternInterfaceReference(ExternInterfaceNode type, int arrayDimensions) {
		super(type, arrayDimensions);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitExternInterfaceReference(this);
	}
}
