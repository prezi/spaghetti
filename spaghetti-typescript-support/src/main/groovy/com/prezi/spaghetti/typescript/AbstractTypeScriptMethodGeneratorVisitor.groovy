package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.MethodParameterNode

abstract class AbstractTypeScriptMethodGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	protected String visitMethodNode(MethodNode node) {
		def returnType = node.returnType.accept(this)
		def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + ">" : ""
		def params = node.parameters*.accept(this).join(", ")

		return \
"""	${node.name}${typeParams}(${params}):${returnType};
"""
	}

	@Override
	String visitMethodParameterNode(MethodParameterNode node) {
		def result = node.name + ':' + node.type.accept(this)
		if (node.optional) {
			result += " = " + TypeScriptUtils.toPrimitiveString(node.optionalValue)
		}
		return result
	}
}
