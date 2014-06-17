package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.TypeParameterNode
import com.prezi.spaghetti.ast.TypeParameterReference

class DefaultTypeParameterReference extends AbstractTypeNodeReference<TypeParameterNode> implements TypeParameterReference {
	DefaultTypeParameterReference(TypeParameterNode type, int arrayDimensions) {
		super(type, arrayDimensions)
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitTypeParameterReference(this)
	}
}
