package com.prezi.spaghetti.typescript.type.enums

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor
import groovy.transform.InheritConstructors

@InheritConstructors
class TypeScriptEnumGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	@Override
	String visitEnumNode(EnumNode node) {
"""export enum ${node.name} {
${node.values*.accept(new EnumValueVisitor(currentNamespace)).join(",\n")}
}
"""
	}

	@InheritConstructors
	private class EnumValueVisitor extends AbstractTypeScriptGeneratorVisitor {
		@Override
		String visitEnumValueNode(EnumValueNode node) {
			return "\t${node.name} = ${node.value.toString()}"
		}
	}
}
