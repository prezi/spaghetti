package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptEnumGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	@Override
	String visitEnumNode(EnumNode node) {
		def valueLines = []
		node.values.eachWithIndex{ value, index ->
			valueLines += value.accept(new TypeScriptEnumValueGeneratorVisitor(index))
		}
"""export enum ${node.name} {
${valueLines.join(",\n")}
}
"""
	}

	private static class TypeScriptEnumValueGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {
		private final int valueIndex

		TypeScriptEnumValueGeneratorVisitor(int valueIndex) {
			this.valueIndex = valueIndex
		}

		@Override
		String visitEnumValueNode(EnumValueNode node) {
			return "\t${node.name} = ${valueIndex}"
		}
	}
}
