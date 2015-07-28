package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode

class TypeScriptEnumGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	@Override
	String visitEnumNode(EnumNode node) {
"""export enum ${node.name} {
${node.values*.accept(createEnumValueVisitor()).join(",\n")}
}
"""
	}

	protected EnumValueVisitor createEnumValueVisitor() {
		return new EnumValueVisitor();
	}

	protected class EnumValueVisitor extends AbstractTypeScriptGeneratorVisitor {
		@Override
		String visitEnumValueNode(EnumValueNode node) {
			return "\t${node.name} = ${generateValueExpression(node)}"
		}

		String generateValueExpression(EnumValueNode node) {
			return node.value.toString()
		}
	}
}
