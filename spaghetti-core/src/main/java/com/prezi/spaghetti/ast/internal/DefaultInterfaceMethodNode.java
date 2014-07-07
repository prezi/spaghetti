package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.InterfaceMethodNode;
import com.prezi.spaghetti.ast.ModuleVisitor;

public class DefaultInterfaceMethodNode extends AbstractMethodNode implements InterfaceMethodNode {
	public DefaultInterfaceMethodNode(String name) {
		super(name);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitInterfaceMethodNode(this);
	}

}
