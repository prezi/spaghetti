package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AnnotationNode
import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.DocumentationNode
import com.prezi.spaghetti.ast.MethodParameterNode
import com.prezi.spaghetti.ast.NamedNodeSet
import com.prezi.spaghetti.ast.TypeParameterNode
import com.prezi.spaghetti.ast.TypeReference

/**
 * Created by lptr on 27/05/14.
 */
abstract class AbstractMethodNode extends AbstractNamedNode implements MutableMethodNode {
	final NamedNodeSet<AnnotationNode> annotations = new DefaultNamedNodeSet<>("annotation")
	DocumentationNode documentation = DocumentationNode.NONE

	final NamedNodeSet<TypeParameterNode> typeParameters = new DefaultNamedNodeSet<>("type parameter")
	TypeReference returnType
	final NamedNodeSet<MethodParameterNode> parameters = new DefaultNamedNodeSet<>("parameter")

	AbstractMethodNode(String name) {
		super(name)
	}

	@Override
	List<? extends AstNode> getChildren() {
		return super.children + typeParameters + [returnType] + parameters
	}
}
