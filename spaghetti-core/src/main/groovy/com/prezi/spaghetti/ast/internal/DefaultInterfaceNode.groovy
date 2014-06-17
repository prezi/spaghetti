package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AnnotationNode
import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.DocumentationNode
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.InterfaceMethodNode
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.InterfaceReference
import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.NamedNodeSet
import com.prezi.spaghetti.ast.TypeParameterNode

class DefaultInterfaceNode extends AbstractTypeNode implements InterfaceNode, MutableDocumentedNode {
	final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<>("annotation")
	DocumentationNode documentation = DocumentationNode.NONE

	final NamedNodeSet<TypeParameterNode> typeParameters = new DefaultNamedNodeSet<>("type parameter")
	final Set<InterfaceReference> superInterfaces = new LinkedHashSet<>()
	final NamedNodeSet<InterfaceMethodNode> methods = new DefaultNamedNodeSet<>("method")

	DefaultInterfaceNode(FQName qualifiedName) {
		super(qualifiedName)
	}

	@Override
	List<? extends AstNode> getChildren() {
		return super.children + typeParameters + superInterfaces + methods
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitInterfaceNode(this)
	}
}
