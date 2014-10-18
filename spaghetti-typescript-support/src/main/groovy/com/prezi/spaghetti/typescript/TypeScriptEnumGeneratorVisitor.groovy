package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode

class TypeScriptEnumGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	@Override
	String visitEnumNode(EnumNode node) {
		def valueLines = []
		node.values.eachWithIndex{ value, index ->
			valueLines += value.accept(new TypeScriptEnumValueGeneratorVisitor(node.name, index))
		}
"""export class ${node.name} {
${valueLines.join("\n")}
}
"""
	}

	private static class TypeScriptEnumValueGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {
		private final String enumName
		private final int valueIndex

		TypeScriptEnumValueGeneratorVisitor(String enumName, int valueIndex) {
			this.enumName = enumName
			this.valueIndex = valueIndex
		}

		@Override
		String visitEnumValueNode(EnumValueNode node) {
			return "\tstatic ${node.name}:${enumName} = ${valueIndex};"
		}
	}
}
