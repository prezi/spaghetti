package com.prezi.spaghetti.haxe.impl

import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.haxe.AbstractHaxeMethodGeneratorVisitor

class HaxeModuleProxyGeneratorVisitor extends AbstractHaxeMethodGeneratorVisitor {

	private final ModuleNode module

	HaxeModuleProxyGeneratorVisitor(ModuleNode module) {
		this.module = module
	}

	@Override
	String visitModuleNode(ModuleNode node) {
		return \
"""@:final class __${node.alias}Proxy {
	public function new() {}
${node.methods*.accept(this).join("")}
}
"""
	}

	@Override
	String visitMethodNode(MethodNode node) {
		def returnType = node.returnType.accept(this)
		returnType = wrapNullableTypeReference(returnType, node)
		def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + ">" : ""
		def params = node.parameters*.accept(this).join(", ")
		def paramNames = node.parameters*.name.join(", ")

"""	public function ${node.name}${typeParams}(${params}):${returnType} {
		${returnType == "Void"?"":"return "}${module.name}.${module.alias}.${node.name}(${paramNames});
	}
"""
	}

	@Override
	String afterVisit(AstNode node, String result) {
		return result
	}
}
