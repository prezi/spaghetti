package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.TypeChain
import com.prezi.spaghetti.ast.TypeReference
import com.prezi.spaghetti.ast.VoidTypeReference

class DefaultTypeChain extends AbstractArrayedTypeReference implements TypeChain {
	final List<TypeReference> elements = []

	DefaultTypeChain(int arrayDimensions) {
		super(arrayDimensions)
	}

	@Override
	List<TypeReference> getParameters() {
		if (elements.size() == 2 && elements[0] == VoidTypeReference.VOID) {
			return []
		}
		return elements.subList(0, elements.size() - 1)
	}

	@Override
	TypeReference getReturnType() {
		return elements.last()
	}

	@Override
	List<? extends AstNode> getChildren() {
		return super.children + elements
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitTypeChain(this)
	}

	@Override
	String toString() {
		return elements*.toString().join("->")
	}
}
