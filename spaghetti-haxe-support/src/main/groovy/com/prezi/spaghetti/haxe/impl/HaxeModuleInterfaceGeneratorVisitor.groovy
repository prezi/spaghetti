package com.prezi.spaghetti.haxe.impl

import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.haxe.AbstractHaxeMethodGeneratorVisitor

class HaxeModuleInterfaceGeneratorVisitor extends AbstractHaxeMethodGeneratorVisitor {

	@Override
	String visitModuleNode(ModuleNode node) {
"""interface I${node.alias} {
${node.methods*.accept(this).join("")}
}
"""
	}
}
