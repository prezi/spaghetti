package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AnnotationNode
import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.ast.DocumentationNode
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.NamedNodeSet

class DefaultConstNode extends AbstractTypeNode implements ConstNode, MutableDocumentedNode {
	final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<>("annotation")
	DocumentationNode documentation = DocumentationNode.NONE

	final NamedNodeSet<DefaultConstEntryNode> entries = new DefaultNamedNodeSet<>("entry")

	DefaultConstNode(FQName qualifiedName) {
		super(qualifiedName)
	}

	@Override
	List<? extends AstNode> getChildren() {
		return super.children + entries
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitConstNode(this)
	}
}
