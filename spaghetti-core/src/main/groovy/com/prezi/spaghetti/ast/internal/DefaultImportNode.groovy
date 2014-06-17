package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.ImportNode
import com.prezi.spaghetti.ast.ModuleVisitor

class DefaultImportNode extends AbstractNode implements ImportNode {
	final FQName qualifiedName
	final String alias

	DefaultImportNode(FQName qualifiedName, String alias) {
		this.qualifiedName = qualifiedName
		this.alias = alias
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitImportNode(this)
	}

	@Override
	String toString() {
		return qualifiedName.fullyQualifiedName
	}
}
