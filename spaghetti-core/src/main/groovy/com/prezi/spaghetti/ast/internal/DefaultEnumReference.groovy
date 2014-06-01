package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumReference
import com.prezi.spaghetti.ast.ModuleVisitor

/**
 * Created by lptr on 29/05/14.
 */
class DefaultEnumReference extends AbstractTypeNodeReference<EnumNode> implements EnumReference {
	DefaultEnumReference(EnumNode type, int arrayDimensions) {
		super(type, arrayDimensions)
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitEnumReference(this)
	}
}
