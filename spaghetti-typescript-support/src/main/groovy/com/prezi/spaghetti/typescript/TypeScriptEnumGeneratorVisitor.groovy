package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode

class TypeScriptEnumGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	@Override
	String visitEnumNode(EnumNode node) {
"""export enum ${node.name} {
${node.normalizedValues*.accept(new EnumValueVisitor()).join(",\n")}
}
"""
	}

	private static class EnumValueVisitor extends AbstractTypeScriptGeneratorVisitor {
		@Override
		String visitEnumValueNode(EnumValueNode node) {
			return "\t${node.name} = ${node.value}"
		}
	}
}
