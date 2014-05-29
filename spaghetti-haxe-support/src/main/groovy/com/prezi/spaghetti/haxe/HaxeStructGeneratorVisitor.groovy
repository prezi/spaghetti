package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.PropertyNode
import com.prezi.spaghetti.ast.StructNode

/**
 * Created by lptr on 16/11/13.
 */
class HaxeStructGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	@Override
	String visitStructNode(StructNode node) {
"""typedef ${node.name} = {
${visitChildren(node)}
}
"""
	}

	@Override
	String visitPropertyNode(PropertyNode node) {
		def mutable = node.annotations.find { it.name == "mutable"} != null
		def modifiers = mutable ? "" : " (default, never)"
"""	var ${node.name}${modifiers}:${node.type.accept(this)};
"""
	}
}
