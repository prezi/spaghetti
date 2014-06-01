package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AnnotationNode
import com.prezi.spaghetti.ast.ConstEntryNode
import com.prezi.spaghetti.ast.DocumentationNode
import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.NamedNodeSet
import com.prezi.spaghetti.ast.PrimitiveTypeReference

/**
 * Created by lptr on 29/05/14.
 */
class DefaultConstEntryNode extends AbstractTypeNamePairNode<PrimitiveTypeReference> implements ConstEntryNode, MutableDocumentedNode {
	final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<>("annotation")
	DocumentationNode documentation = DocumentationNode.NONE

	final Object value

	DefaultConstEntryNode(String name, PrimitiveTypeReference type, Object value) {
		super(name, type)
		this.value = value
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitConstEntryNode(this)
	}
}
