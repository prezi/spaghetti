package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.InterfaceMethodNode
import com.prezi.spaghetti.ast.InterfaceNode

/**
 * Created by lptr on 22/05/14.
 */
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

	@Override
	String visitInterfaceMethodNode(InterfaceMethodNode node) {
		return visitMethodNode(node)
	}
}
