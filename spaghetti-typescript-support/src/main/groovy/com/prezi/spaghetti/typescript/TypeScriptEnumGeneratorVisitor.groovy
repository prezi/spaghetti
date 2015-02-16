package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode

class TypeScriptEnumGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	@Override
	String visitEnumNode(EnumNode node) {
"""export enum ${node.name} {
${node.values*.accept(this).join(",\n")}
}
"""
	}

	@Override
	String visitEnumValueNode(EnumValueNode node) {
		return "\t${node.name}"
	}
}
