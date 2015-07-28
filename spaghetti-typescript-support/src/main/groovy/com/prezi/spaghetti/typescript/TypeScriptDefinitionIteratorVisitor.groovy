package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.StringModuleVisitorBase
import com.prezi.spaghetti.ast.StructNode

class TypeScriptDefinitionIteratorVisitor extends StringModuleVisitorBase {

	@Override
	String visitInterfaceNode(InterfaceNode node) {
		return new TypeScriptInterfaceGeneratorVisitor().visit(node)
	}

	@Override
	String visitEnumNode(EnumNode node) {
		return createTypeScriptEnumGeneratorVisitor().visit(node)
	}

	TypeScriptEnumGeneratorVisitor createTypeScriptEnumGeneratorVisitor() {
		return new TypeScriptEnumGeneratorVisitor()
	}

	@Override
	String visitStructNode(StructNode node) {
		return new TypeScriptStructGeneratorVisitor().visit(node)
	}

	@Override
	String visitConstNode(ConstNode node) {
		return new TypeScriptConstGeneratorVisitor().visit(node)
	}
}
