package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode
import com.prezi.spaghetti.generator.EnumGeneratorUtils

class TypeScriptEnumGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	@Override
	String visitEnumNode(EnumNode node) {
		def namesToValues = EnumGeneratorUtils.calculateEnumValues(node)
		def valueVisitor = new EnumValueVisitor(node.name, namesToValues)
"""export enum ${node.name} {
${node.values*.accept(valueVisitor).join(",\n")}
}
"""
	}

	private static class EnumValueVisitor extends AbstractTypeScriptGeneratorVisitor {
		private final String enumName
		private final Map<String, Integer> namesToValues

		EnumValueVisitor(String enumName, Map<String, Integer> namesToValues) {
			this.enumName = enumName
			this.namesToValues = namesToValues
		}

		@Override
		String visitEnumValueNode(EnumValueNode node) {
			return "\t${node.name} = ${namesToValues[node.name]}"
		}
	}
}
