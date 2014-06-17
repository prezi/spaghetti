package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AnnotationNode
import com.prezi.spaghetti.ast.MethodParameterNode
import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.NamedNodeSet
import com.prezi.spaghetti.ast.TypeReference

class DefaultMethodParameterNode extends AbstractTypeNamePairNode<TypeReference> implements MethodParameterNode {
	final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<>("annotation")

	DefaultMethodParameterNode(String name, TypeReference type) {
		super(name, type)
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitMethodParameterNode(this)
	}
}
