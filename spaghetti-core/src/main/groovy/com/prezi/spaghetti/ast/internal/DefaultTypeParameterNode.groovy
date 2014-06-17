package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.TypeParameterNode

class DefaultTypeParameterNode extends AbstractTypeNode implements TypeParameterNode {
	DefaultTypeParameterNode(String name) {
		super(FQName.fromString(name))
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitTypeParameterNode(this)
	}
}
