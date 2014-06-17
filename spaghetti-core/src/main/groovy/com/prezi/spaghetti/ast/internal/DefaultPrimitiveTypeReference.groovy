package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.PrimitiveType
import com.prezi.spaghetti.ast.PrimitiveTypeReference
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class DefaultPrimitiveTypeReference extends AbstractArrayedTypeReference implements PrimitiveTypeReference {
	final PrimitiveType type

	DefaultPrimitiveTypeReference(PrimitiveType type, int arrayDimensions) {
		super(arrayDimensions)
		this.type = type
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitPrimitiveTypeReference(this)
	}

	@Override
	String toString() {
		return "&" + type.name().toLowerCase() + "[]" * arrayDimensions
	}
}
