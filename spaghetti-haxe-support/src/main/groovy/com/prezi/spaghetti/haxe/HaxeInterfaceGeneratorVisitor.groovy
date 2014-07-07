package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.InterfaceMethodNode
import com.prezi.spaghetti.ast.InterfaceNode

class HaxeInterfaceGeneratorVisitor extends AbstractHaxeMethodGeneratorVisitor {

	@Override
	String visitInterfaceNode(InterfaceNode node) {
		def typeName = node.name
		if (node.typeParameters) {
			typeName += "<" + node.typeParameters*.name.join(", ") + ">"
		}
		def superTypes = node.superInterfaces*.accept(this)
		def methodDefinitions = node.methods*.accept(this).join("")

		return   \
  """${defineType(typeName, superTypes)}
${methodDefinitions}
}
"""
	}

	private static String defineType(String typeName, Collection<String> superTypes) {
		def declaration = "interface ${typeName}"
		superTypes.each { superType ->
			declaration += " extends ${superType}"
		}
		return declaration + " {"
	}

	@Override
	String visitInterfaceMethodNode(InterfaceMethodNode node) {
		return visitMethodNode(node)
	}
}
