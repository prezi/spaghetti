package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AnnotationNode
import com.prezi.spaghetti.ast.DocumentationNode
import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.NamedNodeSet
import com.prezi.spaghetti.ast.PropertyNode
import com.prezi.spaghetti.ast.TypeReference

class DefaultPropertyNode extends AbstractTypeNamePairNode<TypeReference> implements PropertyNode, MutableDocumentedNode {
	final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<>("annotation")
	DocumentationNode documentation = DocumentationNode.NONE

	DefaultPropertyNode(String name, TypeReference type) {
		super(name, type)
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitPropertyNode(this)
	}
}
