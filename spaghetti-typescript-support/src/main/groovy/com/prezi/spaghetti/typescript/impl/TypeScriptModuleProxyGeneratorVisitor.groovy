package com.prezi.spaghetti.typescript.impl

import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.VoidTypeReference
import com.prezi.spaghetti.typescript.AbstractTypeScriptMethodGeneratorVisitor

class TypeScriptModuleProxyGeneratorVisitor extends AbstractTypeScriptMethodGeneratorVisitor {

	private final ModuleNode module

	TypeScriptModuleProxyGeneratorVisitor(ModuleNode module) {
		this.module = module
	}

	@Override
	String visitModuleNode(ModuleNode node) {
"""export class __${node.alias}Proxy {
${node.methods*.accept(this).join("")}
}
"""
	}

	@Override
	String visitMethodNode(MethodNode node) {
		def returnType = node.returnType.accept(this)
		def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + ">" : ""
		def params = node.parameters*.accept(this).join(", ")
		def paramNames = node.parameters*.name.join(", ")

"""	${node.name}${typeParams}(${params}):${returnType} {
		${node.returnType == VoidTypeReference.VOID ? "" : "return "}${module.name}.${module.alias}.${node.name}(${paramNames});
	}
"""
	}

	@Override
	String afterVisit(AstNode node, String result) {
		return result
	}
}
