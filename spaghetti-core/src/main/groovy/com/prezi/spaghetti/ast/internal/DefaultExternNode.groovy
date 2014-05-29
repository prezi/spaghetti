package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AnnotationNode
import com.prezi.spaghetti.ast.DocumentationNode
import com.prezi.spaghetti.ast.ExternNode
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.NamedNodeSet

/**
 * Created by lptr on 29/05/14.
 */
class DefaultExternNode extends AbstractTypeNode implements ExternNode, MutableDocumentedNode {
	final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<>("annotation")
	DocumentationNode documentation = DocumentationNode.NONE

	DefaultExternNode(FQName qualifiedName) {
		super(qualifiedName)
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitExternNode(this)
	}
}
