package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.ExternNode
import com.prezi.spaghetti.ast.ExternReference
import com.prezi.spaghetti.ast.ModuleVisitor

class DefaultExternReference extends AbstractTypeNodeReference<ExternNode> implements ExternReference {

	DefaultExternReference(ExternNode type, int arrayDimensions) {
		super(type, arrayDimensions)
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitExternReference(this)
	}
}
