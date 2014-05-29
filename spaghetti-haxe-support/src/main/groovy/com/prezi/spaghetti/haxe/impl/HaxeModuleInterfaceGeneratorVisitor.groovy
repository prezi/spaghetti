package com.prezi.spaghetti.haxe.impl

import com.prezi.spaghetti.ast.ModuleMethodNode
import com.prezi.spaghetti.ast.ModuleMethodType
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.haxe.AbstractHaxeMethodGeneratorVisitor

/**
 * Created by lptr on 16/11/13.
 */
class HaxeModuleInterfaceGeneratorVisitor extends AbstractHaxeMethodGeneratorVisitor {

	@Override
	String visitModuleNode(ModuleNode node) {
"""interface I${node.alias} {
${node.methods*.accept(this).join("")}
}
"""
	}

	@Override
	String visitModuleMethodNode(ModuleMethodNode node) {
		return node.type == ModuleMethodType.STATIC ? "" : visitMethodNode(node)
	}
}
