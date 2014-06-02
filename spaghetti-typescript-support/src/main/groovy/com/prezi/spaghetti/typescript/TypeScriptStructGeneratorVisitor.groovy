package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.PropertyNode
import com.prezi.spaghetti.ast.StructNode

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptStructGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

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
"""	${node.name}: ${node.type.accept(this)};
"""
	}
}
