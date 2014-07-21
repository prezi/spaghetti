package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.PropertyNode
import com.prezi.spaghetti.ast.StructNode

class HaxeStructGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	@Override
	String visitStructNode(StructNode node) {
		def typeName = node.name
		if (node.typeParameters) {
			typeName += "<" + node.typeParameters*.name.join(", ") + ">"
		}
"""typedef ${typeName} = {
${visitChildren(node)}
}
"""
	}

	@Override
	String visitPropertyNode(PropertyNode node) {
		def mutable = node.annotations.contains("mutable")
		def name = node.name
		def modifiers = mutable ? "" : " (default, never)"
		def type = node.type.accept(this)
		type = wrapNullableTypeReference(type, node)
		"""	var ${name}${modifiers}:${type};
"""
	}
}
