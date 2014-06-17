package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.MethodParameterNode

abstract class AbstractHaxeMethodGeneratorVisitor extends AbstractHaxeGeneratorVisitor {

	protected String visitMethodNode(MethodNode node) {
		def returnType = node.returnType.accept(this)
		def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + ">" : ""
		def params = node.parameters*.accept(this).join(", ")

		return \
"""	function ${node.name}${typeParams}(${params}):${returnType};
"""
	}

	@Override
	String visitMethodParameterNode(MethodParameterNode node) {
		return "${node.name}:${node.type.accept(this)}"
	}
}
