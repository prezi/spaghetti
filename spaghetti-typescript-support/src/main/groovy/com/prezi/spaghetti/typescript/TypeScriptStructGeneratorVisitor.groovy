package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.PropertyNode
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.ast.MethodNode

class TypeScriptStructGeneratorVisitor extends AbstractTypeScriptMethodGeneratorVisitor {

	@Override
	String visitStructNode(StructNode node) {
		def typeName = node.name
		if (node.typeParameters) {
			typeName += "<" + node.typeParameters*.name.join(", ") + ">"
		}
"""export interface ${typeName} {
${visitChildren(node)}
}
"""
	}

	@Override
	String visitPropertyNode(PropertyNode node) {
		def optional = node.optional ? "?" : ""
"""	${node.name}${optional}: ${node.type.accept(this)};
"""
	}
}
