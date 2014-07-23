package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.MethodParameterNode

abstract class AbstractHaxeMethodGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	protected String visitMethodNode(MethodNode node) {
		def returnType = node.returnType.accept(this)
		returnType = wrapNullableTypeReference(returnType, node)
		def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + ">" : ""
		def params = node.parameters*.accept(this).join(", ")

		return \
"""	function ${node.name}${typeParams}(${params}):${returnType};
"""
	}

	@Override
	String visitMethodParameterNode(MethodParameterNode node) {
		def type = node.type.accept(this)
		type = wrapNullableTypeReference(type, node)
		def result = node.name + ':' + type
		if (node.isOptional()) {
			result = "?" + result
		}
		return result
	}
}
