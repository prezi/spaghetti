package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.StringModuleVisitorBase
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.typescript.type.consts.TypeScriptConstGeneratorVisitor
import com.prezi.spaghetti.typescript.type.enums.TypeScriptEnumGeneratorVisitor

class TypeScriptDefinitionIteratorVisitor extends StringModuleVisitorBase {
	private String namespace

	TypeScriptDefinitionIteratorVisitor(String namespace) {
		this.namespace = namespace
	}

	@Override
	String visitInterfaceNode(InterfaceNode node) {
		return new TypeScriptInterfaceGeneratorVisitor(namespace).visit(node)
	}

	@Override
	String visitEnumNode(EnumNode node) {
		return new TypeScriptEnumGeneratorVisitor(namespace).visit(node)
	}

	@Override
	String visitStructNode(StructNode node) {
		return new TypeScriptStructGeneratorVisitor(namespace).visit(node)
	}

	@Override
	String visitConstNode(ConstNode node) {
		return new TypeScriptConstGeneratorVisitor(namespace).visit(node)
	}
}
