package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.ConstEntryNode
import com.prezi.spaghetti.ast.ConstNode

class TypeScriptConstGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {
	@Override
	String visitConstNode(ConstNode node) {
"""export class ${node.name} {
${visitChildren(node)}
}
"""
	}

	@Override
	String visitConstEntryNode(ConstEntryNode node) {
		String type = PRIMITIVE_TYPES.get(node.type.type)
		String value = TypeScriptUtils.toPrimitiveString(node.value)
		return "\tstatic ${node.name}: ${type} = ${value};\n"
	}
}
