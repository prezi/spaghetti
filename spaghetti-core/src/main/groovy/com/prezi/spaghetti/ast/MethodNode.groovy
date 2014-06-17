package com.prezi.spaghetti.ast

interface MethodNode extends NamedNode, AstNode {
	NamedNodeSet<TypeParameterNode> getTypeParameters()
	TypeReference getReturnType()
	NamedNodeSet<MethodParameterNode> getParameters()
}
