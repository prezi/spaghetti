package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.StringModuleVisitorBase
import com.prezi.spaghetti.ast.StructNode

/**
 * Created by lptr on 20/11/13.
 */
class TypeScriptDefinitionIteratorVisitor extends StringModuleVisitorBase {

	@Override
	String visitInterfaceNode(InterfaceNode node) {
		return new TypeScriptInterfaceGeneratorVisitor().visit(node)
	}

	@Override
	String visitEnumNode(EnumNode node) {
		return new TypeScriptEnumGeneratorVisitor().visit(node)
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
