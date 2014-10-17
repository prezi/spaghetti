package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.ConstEntryNode
import com.prezi.spaghetti.ast.ConstNode

class KotlinConstGeneratorVisitor extends AbstractKotlinGeneratorVisitor {
	@Override
	String visitConstNode(ConstNode node) {
"""object ${node.name} {
${visitChildren(node)}
}
"""
	}

	@Override
	String visitConstEntryNode(ConstEntryNode node) {
		String type = PRIMITIVE_TYPES.get(node.type.type)
		String value = KotlinUtils.toPrimitiveString(node.value)
		return "\tval ${node.name}:${type} = ${value}\n"
	}
}
