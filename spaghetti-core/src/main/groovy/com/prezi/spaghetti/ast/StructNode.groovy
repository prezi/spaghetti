package com.prezi.spaghetti.ast

interface StructNode extends AnnotatedNode, DocumentedNode, ReferableTypeNode {
	NamedNodeSet<TypeParameterNode> getTypeParameters()
	NamedNodeSet<PropertyNode> getProperties()
}
