package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AnnotationNode
import com.prezi.spaghetti.ast.DocumentationNode
import com.prezi.spaghetti.ast.EnumValueNode
import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.NamedNodeSet

/**
 * Created by lptr on 31/05/14.
 */
class DefaultEnumValueNode extends AbstractNamedNode implements EnumValueNode, MutableDocumentedNode {
	final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<>("annotation")
	DocumentationNode documentation = DocumentationNode.NONE

	DefaultEnumValueNode(String name) {
		super(name)
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitEnumValueNode(this)
	}
}
