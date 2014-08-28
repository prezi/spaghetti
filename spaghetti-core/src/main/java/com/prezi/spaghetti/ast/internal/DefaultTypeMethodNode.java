package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.TypeMethodNode;

public class DefaultTypeMethodNode extends AbstractMethodNode implements TypeMethodNode {
	public DefaultTypeMethodNode(String name) {
		super(name);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitTypeMethodNode(this);
	}

}
