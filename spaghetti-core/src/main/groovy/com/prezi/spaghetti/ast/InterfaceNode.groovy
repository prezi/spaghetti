package com.prezi.spaghetti.ast

interface InterfaceNode extends AnnotatedNode, DocumentedNode, ReferableTypeNode, MethodContainer<InterfaceMethodNode> {
	NamedNodeSet<TypeParameterNode> getTypeParameters()
	Set<InterfaceReference> getSuperInterfaces()
}
