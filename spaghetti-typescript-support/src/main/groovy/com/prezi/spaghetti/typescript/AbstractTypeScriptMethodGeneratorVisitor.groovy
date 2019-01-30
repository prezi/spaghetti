package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.MethodParameterNode
import groovy.transform.InheritConstructors

@InheritConstructors
abstract class AbstractTypeScriptMethodGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	@Override
	String visitMethodNode(MethodNode node) {
		def returnType = node.returnType.accept(this)
		def typeParams = node.typeParameters ? "<" + node.typeParameters*.name.join(", ") + ">" : ""
		def params = node.parameters*.accept(this).join(", ")
		def prefix = getMethodPrefix()

		return \
"""${prefix}${node.name}${typeParams}(${params}):${returnType};
"""
	}

	String getMethodPrefix() {
		return "\t"
	}

	@Override
	String visitMethodParameterNode(MethodParameterNode node) {
		return node.name + (node.optional ? "?" : "") + ':' + node.type.accept(this)
	}
}
