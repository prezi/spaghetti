package com.prezi.spaghetti.ast;

import com.prezi.spaghetti.ast.internal.AbstractNode;

public interface VoidTypeReference extends TypeReference {
	public static final VoidTypeReference VOID = new Impl();

	public static class Impl extends AbstractNode implements VoidTypeReference {
		@Override
		public <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
			return visitor.visitVoidTypeReference(this);
		}

		@Override
		public String toString() {
			return "void";
		}
	}
}
