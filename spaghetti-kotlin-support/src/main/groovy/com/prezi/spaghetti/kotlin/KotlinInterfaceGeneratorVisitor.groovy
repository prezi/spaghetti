package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.InterfaceNode

class KotlinInterfaceGeneratorVisitor extends AbstractKotlinGeneratorVisitor {

	@Override
	String visitInterfaceNode(InterfaceNode node) {
		def typeName = node.name

		if (node.typeParameters) {
			typeName += "<" + node.typeParameters*.name.join(", ") + ">"
		}
		def superTypes = node.superInterfaces*.accept(this)
        def methodVisitor = new KotlinInterfaceMethodGeneratorVisitor(typeName, node.superInterfaces)
		def methodDefinitions = node.methods*.accept(methodVisitor).join("")

		return   \
  """${defineType(typeName, superTypes)}
${methodDefinitions}
}
"""
	}

	private static String defineType(String typeName, Collection<?> superTypes) {
		def declaration = "trait ${typeName}"
		if (!superTypes.empty) {
			declaration += ': ' + superTypes.join(", ")
		}
		return declaration + " {"
	}
}
