package com.prezi.spaghetti.kotlin.impl

import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.StringModuleVisitorBase
import com.prezi.spaghetti.kotlin.AbstractKotlinGeneratorVisitor
import com.prezi.spaghetti.kotlin.AbstractKotlinMethodGeneratorVisitor

class KotlinModuleProxyGeneratorVisitor extends AbstractKotlinGeneratorVisitor {

	@Override
	String visitModuleNode(ModuleNode node) {
		return \
"""class __${node.alias}Proxy {
${node.methods*.accept(new MethodVisitor(node)).join("")}
${node.accept(new QualifiedEnumVisitor(node))}}
"""
	}

	private static class QualifiedEnumVisitor extends StringModuleVisitorBase {
		private ModuleNode qualifyingNode

		public QualifiedEnumVisitor(ModuleNode qualifyingNode) {
			this.qualifyingNode = qualifyingNode
		}

		@Override
		String visitEnumNode(EnumNode enumNode) {
			return "	public val ${enumNode.name}: ${qualifyingNode.name}.${enumNode.name}.Companion = ${qualifyingNode.name}.${enumNode.name}\n"
		}

		@Override
		String afterVisit(AstNode n, String result) {
			return result
		}
	}

	private static class MethodVisitor extends AbstractKotlinMethodGeneratorVisitor {
		private final ModuleNode module

		MethodVisitor(ModuleNode module) {
			this.module = module
		}

		@Override
		String visitMethodNode(MethodNode node) {
			def returnType = node.returnType.accept(this)
			returnType = wrapNullableTypeReference(returnType, node)
			def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + "> " : ""
			def params = node.parameters*.accept(this).join(", ")
			def paramNames = node.parameters*.name.join(", ")

			return \
"""	fun ${typeParams}${node.name}(${params}):${returnType} {
		${returnType == "Void"?"":"return "}${module.name}.${module.alias}.${node.name}(${paramNames})
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
