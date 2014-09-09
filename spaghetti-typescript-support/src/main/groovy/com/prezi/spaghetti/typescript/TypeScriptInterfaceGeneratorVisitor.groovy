package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.MethodNode

class TypeScriptInterfaceGeneratorVisitor extends AbstractTypeScriptMethodGeneratorVisitor {

	@Override
	String visitInterfaceNode(InterfaceNode node) {
		def typeName = node.name
		if (node.typeParameters) {
			typeName += "<" + node.typeParameters*.name.join(", ") + ">"
		}
		def superTypes = node.superInterfaces*.accept(this)
		def methodDefinitions = node.methods*.accept(this).join("")

		return  \
 """${defineType(typeName, superTypes)}
${methodDefinitions}
}
"""
	}

	private static String defineType(String typeName, Collection<String> superTypes) {
		def declaration = "export interface ${typeName}"
		if (!superTypes.empty) {
			declaration += " extends ${superTypes.join(", ")}"
		}
		declaration += " {"
		return declaration
	}
}
