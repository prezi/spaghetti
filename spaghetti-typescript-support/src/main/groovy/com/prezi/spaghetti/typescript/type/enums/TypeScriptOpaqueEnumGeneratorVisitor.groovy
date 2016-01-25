package com.prezi.spaghetti.typescript.type.enums

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor

class TypeScriptOpaqueEnumGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {
	@Override
	String visitEnumNode(EnumNode node) {
"""export enum ${node.name} {}"""
	}
}
