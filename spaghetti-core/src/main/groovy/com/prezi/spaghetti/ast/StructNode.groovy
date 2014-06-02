package com.prezi.spaghetti.ast

/**
 * Created by lptr on 29/05/14.
 */
interface StructNode extends AnnotatedNode, DocumentedNode, ReferableTypeNode {
	NamedNodeSet<TypeParameterNode> getTypeParameters()
	NamedNodeSet<PropertyNode> getProperties()
}
