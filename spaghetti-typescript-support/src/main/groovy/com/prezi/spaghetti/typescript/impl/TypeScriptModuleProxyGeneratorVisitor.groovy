package com.prezi.spaghetti.typescript.impl

import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.StringModuleVisitorBase
import com.prezi.spaghetti.ast.VoidTypeReference
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor
import com.prezi.spaghetti.typescript.AbstractTypeScriptMethodGeneratorVisitor

class TypeScriptModuleProxyGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	@Override
	String visitModuleNode(ModuleNode node) {
"""export class __${node.alias}Proxy {
${
	node.methods*.accept(new MethodVisitor(node)).join("") +
	node.accept(new QualifiedConstVisitor(node)) +
	node.accept(new QualifiedEnumVisitor(node))
}}
"""
	}

	private static class QualifiedConstVisitor extends StringModuleVisitorBase {
		private ModuleNode qualifyingNode

		public QualifiedConstVisitor(ModuleNode qualifyingNode) {
			this.qualifyingNode = qualifyingNode
		}

		@Override
		String visitConstNode(ConstNode constNode) {
			return "\tpublic ${constNode.name} = ${qualifyingNode.name}.${constNode.name};\n"
		}

		@Override
		String afterVisit(AstNode n, String result) {
			return result
		}
	}

	private static class QualifiedEnumVisitor extends StringModuleVisitorBase {
		private ModuleNode qualifyingNode

		public QualifiedEnumVisitor(ModuleNode qualifyingNode) {
			this.qualifyingNode = qualifyingNode
		}

		@Override
		String visitEnumNode(EnumNode enumNode) {
			return "\tpublic ${enumNode.name} = ${qualifyingNode.name}.${enumNode.name};\n"
		}

		@Override
		String afterVisit(AstNode n, String result) {
			return result
		}
	}

	private static class MethodVisitor extends AbstractTypeScriptMethodGeneratorVisitor {
		private final ModuleNode module

		MethodVisitor(ModuleNode module) {
			this.module = module
		}

		@Override
		String visitMethodNode(MethodNode node) {
			def returnType = node.returnType.accept(this)
			def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + ">" : ""
			def params = node.parameters*.accept(this).join(", ")
			def paramNames = node.parameters*.name.join(", ")

			return \
"""	${node.name}${typeParams}(${params}):${returnType} {
		${node.returnType instanceof VoidTypeReference ? "" : "return "}${module.name}.${module.alias}.${node.name}(${paramNames});
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
