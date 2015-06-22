package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.PropertyNode
import com.prezi.spaghetti.ast.StructNode

class KotlinStructGeneratorVisitor extends AbstractKotlinMethodGeneratorVisitor {

	@Override
	String visitStructNode(StructNode node) {
		def typeName = node.name
		if (node.typeParameters) {
			typeName += "<" + node.typeParameters*.name.join(", ") + ">"
		}
		def superStruct = node.superStruct == null ? "" : ": " + node.superStruct.accept(this) + " "
		def members = node.children.findAll({ it instanceof PropertyNode || it instanceof MethodNode })*.accept(this).join("")
"""interface ${typeName} ${superStruct}{
${members}
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
