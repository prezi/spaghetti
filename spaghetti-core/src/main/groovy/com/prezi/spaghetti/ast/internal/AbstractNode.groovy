package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AnnotatedNode
import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.DocumentedNode
import com.prezi.spaghetti.ast.ModuleVisitor

/**
 * Created by lptr on 30/05/14.
 */
abstract class AbstractNode implements AstNode {
	@Override
	def <T> T accept(ModuleVisitor<? extends T> visitor) {
		def result = visitor.beforeVisit(this)
		try {
			result = visitor.aggregateResult(result, acceptInternal(visitor))
		} finally {
			result = visitor.afterVisit(this, result)
		}
		return result
	}

	@Override
	List<? extends AstNode> getChildren() {
		def children = []
		def self = this
		if (self instanceof AnnotatedNode) {
			children.addAll(self.annotations)
		}
		if (self instanceof DocumentedNode) {
			children.add(self.documentation)
		}
		return children
	}

	abstract def <T> T acceptInternal(ModuleVisitor<? extends T> visitor)

	@Override
	abstract String toString()
}
