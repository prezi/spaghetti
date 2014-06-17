package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.ModuleMethodNode;
import com.prezi.spaghetti.ast.ModuleMethodType;
import com.prezi.spaghetti.ast.ModuleVisitor;

public class DefaultModuleMethodNode extends AbstractMethodNode implements ModuleMethodNode {
	private final ModuleMethodType type;

	public DefaultModuleMethodNode(String name, ModuleMethodType type) {
		super(name);
		this.type = type;
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitModuleMethodNode(this);
	}

	@Override
	public ModuleMethodType getType() {
		return type;
	}
}
