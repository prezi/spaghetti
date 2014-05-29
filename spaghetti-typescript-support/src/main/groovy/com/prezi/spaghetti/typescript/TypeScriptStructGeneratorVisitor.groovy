package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.PropertyNode
import com.prezi.spaghetti.ast.StructNode

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptStructGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	@Override
	String visitStructNode(StructNode node) {
"""export interface ${node.name} {
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
