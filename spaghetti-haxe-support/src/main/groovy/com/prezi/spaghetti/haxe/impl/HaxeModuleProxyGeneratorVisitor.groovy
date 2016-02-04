package com.prezi.spaghetti.haxe.impl

import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.StringModuleVisitorBase
import com.prezi.spaghetti.haxe.AbstractHaxeGeneratorVisitor
import com.prezi.spaghetti.haxe.AbstractHaxeMethodGeneratorVisitor

class HaxeModuleProxyGeneratorVisitor extends AbstractHaxeGeneratorVisitor {
	@Override
	String visitModuleNode(ModuleNode node) {
		return \
"""@:final class __${node.alias}Proxy {
	public function new() {}
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
			return "	public var ${constNode.name} = ${qualifyingNode.name}.${constNode.name};\n"
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
			return "	public var ${enumNode.name} = ${qualifyingNode.name}.${enumNode.name};\n"
		}

		@Override
		String afterVisit(AstNode n, String result) {
			return result
		}
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
