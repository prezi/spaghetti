package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.PropertyNode
import com.prezi.spaghetti.ast.StructNode

class KotlinStructGeneratorVisitor extends AbstractKotlinMethodGeneratorVisitor {

	@Override
	String visitStructNode(StructNode node) {
		def typeName = node.name
		if (node.typeParameters) {
			typeName += "<" + node.typeParameters*.name.join(", ") + ">"
		}
"""trait ${typeName} {
${visitChildren(node)}
}
"""
	}

	@Override
	String visitPropertyNode(PropertyNode node) {
		def isOptional = node.optional
		def isMutable = node.annotations.contains("mutable")
		def isNullable = node.annotations.contains("nullable")
		def optional = (isOptional || isNullable) ? "?" : ""
		if (isOptional) {
			optional += " = null"
		}
		def decl = isMutable ? "var" : "val"
"""	${decl} ${node.name}: ${node.type.accept(this)}${optional}
"""
	}
}
