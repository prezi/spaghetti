package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AnnotationNode
import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.DocumentationNode
import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.EnumValueNode
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.NamedNodeSet

/**
 * Created by lptr on 29/05/14.
 */
class DefaultEnumNode extends AbstractTypeNode implements EnumNode, MutableDocumentedNode {
	final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<>("annotation")
	DocumentationNode documentation = DocumentationNode.NONE

	final NamedNodeSet<EnumValueNode> values = new DefaultNamedNodeSet<>("enum value")

	DefaultEnumNode(FQName qualifiedName) {
		super(qualifiedName)
	}

	@Override
	List<? extends AstNode> getChildren() {
		return super.children + values
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitEnumNode(this)
	}
}
