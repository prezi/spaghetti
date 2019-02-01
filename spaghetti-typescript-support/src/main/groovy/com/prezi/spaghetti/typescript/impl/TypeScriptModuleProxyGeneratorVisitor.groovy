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
import groovy.transform.InheritConstructors

@InheritConstructors
class TypeScriptModuleProxyGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	@Override
	String visitModuleNode(ModuleNode node) {
"""import { ${node.alias} } from "${node.alias}";
export class __${node.alias}Proxy {
${
	node.methods*.accept(new MethodVisitor(currentNamespace, node.alias)).join("") +
	node.accept(new ConstVisitor()) +
	node.accept(new EnumVisitor())
}}
"""
	}

	private static class ConstVisitor extends StringModuleVisitorBase {
		@Override
		String visitConstNode(ConstNode constNode) {
			return "\tpublic ${constNode.name} = ${constNode.name};\n"
		}

		@Override
		String afterVisit(AstNode n, String result) {
			return result
		}
	}

	private static class EnumVisitor extends StringModuleVisitorBase {
		@Override
		String visitEnumNode(EnumNode enumNode) {
			return "\tpublic ${enumNode.name} = ${enumNode.name};\n"
		}

		@Override
		String afterVisit(AstNode n, String result) {
			return result
		}
	}

	@InheritConstructors
	private static class MethodVisitor extends AbstractTypeScriptMethodGeneratorVisitor {
		private String moduleName

		MethodVisitor(String namespace, String moduleName) {
			super(namespace)
			this.moduleName = moduleName
		}

		@Override
		String visitMethodNode(MethodNode node) {
			def returnType = node.returnType.accept(this)
			def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + ">" : ""
			def params = node.parameters*.accept(this).join(", ")
			def paramNames = node.parameters*.name.join(", ")

			return \
"""	${node.name}${typeParams}(${params}):${returnType} {
		${node.returnType instanceof VoidTypeReference ? "" : "return "}${moduleName}.${node.name}${typeParams}(${paramNames});
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
