package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.VoidTypeReference;

public class DefaultVoidTypeReference extends AbstractNode implements VoidTypeReference {
	@Override
	public <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitVoidTypeReference(this);
	}

	@Override
	public String toString() {
		return "void";
	}
}
