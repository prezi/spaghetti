package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.PropertyNode
import com.prezi.spaghetti.ast.StructNode

class HaxeStructGeneratorVisitor extends AbstractHaxeMethodGeneratorVisitor {

	@Override
	String visitStructNode(StructNode node) {
		def typeName = node.name
		if (node.typeParameters) {
			typeName += "<" + node.typeParameters*.name.join(", ") + ">"
		}
		def superStructs = node.superStructs*.accept(this).collect({ s -> " > ${s}," }).join("")
		def members = node.children.findAll({ it instanceof PropertyNode || it instanceof MethodNode })*.accept(this).join("")
"""typedef ${typeName} = {${superStructs}
${members}
}
"""
	}

	@Override
	String visitPropertyNode(PropertyNode node) {
		def mutable = node.annotations.contains("mutable")
		def name = node.name
		def modifiers = mutable ? "" : " (default, never)"
		def type = node.type.accept(this)
		def optional = node.optional ? "@:optional " : ""
		type = wrapNullableTypeReference(type, node)
		"""	${optional}var ${name}${modifiers}:${type};
"""
	}
}
