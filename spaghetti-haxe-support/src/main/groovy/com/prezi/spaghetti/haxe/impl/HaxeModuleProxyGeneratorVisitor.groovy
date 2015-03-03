package com.prezi.spaghetti.haxe.impl

import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.haxe.AbstractHaxeGeneratorVisitor
import com.prezi.spaghetti.haxe.AbstractHaxeMethodGeneratorVisitor

class HaxeModuleProxyGeneratorVisitor extends AbstractHaxeGeneratorVisitor {
	@Override
	String visitModuleNode(ModuleNode node) {
		return \
"""@:final class __${node.alias}Proxy {
	public function new() {}
${node.methods*.accept(new MethodVisitor(node)).join("")}
}
"""
	}

	private static class MethodVisitor extends AbstractHaxeMethodGeneratorVisitor {
		private final ModuleNode module

		MethodVisitor(ModuleNode module) {
			this.module = module
		}

		@Override
		String visitMethodNode(MethodNode node) {
			def returnType = node.returnType.accept(this)
			returnType = wrapNullableTypeReference(returnType, node)
			def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + ">" : ""
			def params = node.parameters*.accept(this).join(", ")
			def paramNames = node.parameters*.name.join(", ")

			return \
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

	@Override
	String afterVisit(AstNode node, String result) {
		return result
	}
}
