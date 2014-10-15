package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.InterfaceNode;
import com.prezi.spaghetti.ast.InterfaceReference;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.TypeReference;

public class DefaultInterfaceReference extends AbstractParametrizedTypeNodeReference<InterfaceNode> implements InterfaceReference {
	public DefaultInterfaceReference(InterfaceNode type, int arrayDimensions) {
		super(type, arrayDimensions);
	}

	@Override
	public TypeReference withAdditionalArrayDimensions(int extraDimensions) {
		return new DefaultInterfaceReference(getType(), getArrayDimensions() + extraDimensions);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitInterfaceReference(this);
	}
}
