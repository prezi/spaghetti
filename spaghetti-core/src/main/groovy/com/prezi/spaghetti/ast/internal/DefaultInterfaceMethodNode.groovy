package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.InterfaceMethodNode
import com.prezi.spaghetti.ast.ModuleVisitor

class DefaultInterfaceMethodNode extends AbstractMethodNode implements InterfaceMethodNode {
	DefaultInterfaceMethodNode(String name) {
		super(name)
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitInterfaceMethodNode(this)
	}
}
