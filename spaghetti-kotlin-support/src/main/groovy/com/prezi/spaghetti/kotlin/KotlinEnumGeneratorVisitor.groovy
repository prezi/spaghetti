package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode

class KotlinEnumGeneratorVisitor extends AbstractKotlinGeneratorVisitor {
	@Override
	String visitEnumNode(EnumNode node) {
		def enumName = node.name

		def values = []
		node.values.eachWithIndex { value, index ->
			values.add value.accept(new EnumValueVisitor(enumName, index))
		}

		return \
"""object ${enumName} {
${values.join("\n")}
}
"""
	}

	private static class EnumValueVisitor extends AbstractKotlinGeneratorVisitor {
		private final String enumName
		private final int index

		EnumValueVisitor(String enumName, int index) {
			this.enumName = enumName
			this.index = index
		}

		@Override
		String visitEnumValueNode(EnumValueNode node) {
			return "\tval ${node.name} = ${index}"
		}
	}
}
