package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.ModuleMethodNode
import com.prezi.spaghetti.ast.ModuleMethodType
import com.prezi.spaghetti.ast.ModuleVisitor

class DefaultModuleMethodNode extends AbstractMethodNode implements ModuleMethodNode {
	final ModuleMethodType type

	DefaultModuleMethodNode(String name, ModuleMethodType type) {
		super(name)
		this.type = type
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitModuleMethodNode(this)
	}
}
