package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.TypeParameterNode

/**
 * Created by lptr on 29/05/14.
 */
class DefaultTypeParameterNode extends AbstractTypeNode implements TypeParameterNode {
	DefaultTypeParameterNode(String name) {
		super(FQName.fromString(name))
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitTypeParameterNode(this)
	}
}
