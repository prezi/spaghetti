package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.ConstEntryNode
import com.prezi.spaghetti.ast.ConstNode

class KotlinConstGeneratorVisitor extends AbstractKotlinGeneratorVisitor {
	@Override
	String visitConstNode(ConstNode node) {
		def constName = node.name

		def values = []
		node.entries.eachWithIndex { value, index ->
			values.add value.accept(new ConstValueVisitor(constName, index))
		}

		return \
"""object ${constName} {
${values.join("\n")}
}
"""
	}

	private static class ConstValueVisitor extends AbstractKotlinGeneratorVisitor {
		private final String constName
		private final int index

		ConstValueVisitor(String constName, int index) {
			this.constName = constName
			this.index = index
		}

		@Override
		String visitConstEntryNode(ConstEntryNode node) {
			return "\tval ${node.name} = ${index}"
		}
	}
}
