package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AnnotationNode
import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.DocumentationNode
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.NamedNodeSet
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.ast.TypeParameterNode

class DefaultStructNode extends AbstractTypeNode implements StructNode, MutableDocumentedNode {
	final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<>("annotation")
	DocumentationNode documentation = DocumentationNode.NONE

	final NamedNodeSet<TypeParameterNode> typeParameters = new DefaultNamedNodeSet<>("type parameter")
	final NamedNodeSet<DefaultPropertyNode> properties = new DefaultNamedNodeSet<>("property")

	DefaultStructNode(FQName qualifiedName) {
		super(qualifiedName)
	}

	@Override
	List<? extends AstNode> getChildren() {
		return super.children + properties
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitStructNode(this)
	}
}
