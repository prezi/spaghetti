package com.prezi.spaghetti.typescript.type.consts

import com.prezi.spaghetti.ast.ConstEntryNode
import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor
import com.prezi.spaghetti.typescript.TypeScriptUtils

class TypeScriptConstGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {
	@Override
	String visitConstNode(ConstNode node) {
"""export module ${node.name} {
${visitChildren(node)}
}
"""
	}

	@Override
	String visitConstEntryNode(ConstEntryNode node) {
		String type = PRIMITIVE_TYPES.get(node.type.type)
		String value = TypeScriptUtils.toPrimitiveString(node.value)
		return "\texport const ${node.name}: ${type} = ${value};\n"
	}
}
