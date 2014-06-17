package com.prezi.spaghetti.ast

import com.prezi.spaghetti.ast.internal.AbstractNode

interface VoidTypeReference extends TypeReference {
	public static final VoidTypeReference VOID = new Impl();

	static class Impl extends AbstractNode implements VoidTypeReference {
		@Override
		def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
			return visitor.visitVoidTypeReference(this)
		}

		@Override
		String toString() {
			return "void"
		}
	}
}
