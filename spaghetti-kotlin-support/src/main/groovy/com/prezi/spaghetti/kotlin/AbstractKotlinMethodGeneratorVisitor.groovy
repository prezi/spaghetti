package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.MethodParameterNode

abstract class AbstractKotlinMethodGeneratorVisitor extends AbstractKotlinGeneratorVisitor {

	@Override
	String visitMethodNode(MethodNode node) {
		def returnType = node.returnType.accept(this)
		returnType = wrapNullableTypeReference(returnType, node)
		def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + "> " : ""
		def params = node.parameters*.accept(this).join(", ")

        def fundef = isOverridden(node) ? "override fun" : "fun"

		return \
"""	${fundef} ${typeParams}${node.name}(${params}):${returnType}
"""
	}

	@Override
	String visitMethodParameterNode(MethodParameterNode node) {
		def type = node.type.accept(this)
		type = wrapNullableTypeReference(type, node)
		def result = safeKotlinName(node.name) + ':' + type
		if (node.isOptional()) {
			result = result + "? = null"
		}
		return result
	}

    boolean isOverridden(MethodNode node) {
        return false
    }
}
