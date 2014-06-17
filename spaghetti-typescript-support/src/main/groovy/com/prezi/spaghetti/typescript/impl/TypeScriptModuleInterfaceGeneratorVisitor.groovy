package com.prezi.spaghetti.typescript.impl

import com.prezi.spaghetti.ast.ModuleMethodNode
import com.prezi.spaghetti.ast.ModuleMethodType
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.typescript.AbstractTypeScriptMethodGeneratorVisitor

class TypeScriptModuleInterfaceGeneratorVisitor extends AbstractTypeScriptMethodGeneratorVisitor {

	@Override
	String visitModuleNode(ModuleNode node) {
"""export interface I${node.alias} {
${node.methods*.accept(this).join("")}
}
"""
	}

	@Override
	String visitModuleMethodNode(ModuleMethodNode node) {
		return node.type == ModuleMethodType.STATIC ? "" : visitMethodNode(node)
	}
}
