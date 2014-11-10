package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.MethodNode

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

	@Override
	String visitMethodNode(MethodNode node) {
		def returnType = node.returnType.accept(this)
		returnType = wrapNullableTypeReference(returnType, node)
		def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + "> " : ""
		def params = node.parameters*.accept(this).join(", ")

		return \
"""	native fun ${typeParams}${node.name}(${params}):${returnType}
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
