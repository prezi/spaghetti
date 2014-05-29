package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.InterfaceMethodNode
import com.prezi.spaghetti.ast.ModuleVisitor

/**
 * Created by lptr on 27/05/14.
 */
class DefaultInterfaceMethodNode extends AbstractMethodNode implements InterfaceMethodNode {
	DefaultInterfaceMethodNode(String name) {
		super(name)
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitInterfaceMethodNode(this)
	}
}
