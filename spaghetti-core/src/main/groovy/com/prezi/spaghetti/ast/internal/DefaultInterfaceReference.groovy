package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.InterfaceReference
import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.TypeReference

class DefaultInterfaceReference extends AbstractTypeNodeReference<InterfaceNode> implements InterfaceReference {
	final List<TypeReference> arguments = []

	DefaultInterfaceReference(InterfaceNode type, int arrayDimensions) {
		super(type, arrayDimensions)
	}

	@Override
	List<? extends AstNode> getChildren() {
		return super.children + arguments
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitInterfaceReference(this)
	}
}
