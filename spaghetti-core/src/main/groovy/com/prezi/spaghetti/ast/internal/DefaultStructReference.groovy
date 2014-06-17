package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.ast.StructReference

class DefaultStructReference extends AbstractTypeNodeReference<StructNode> implements StructReference {
	DefaultStructReference(StructNode type, int arrayDimensions) {
		super(type, arrayDimensions)
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitStructReference(this)
	}
}
