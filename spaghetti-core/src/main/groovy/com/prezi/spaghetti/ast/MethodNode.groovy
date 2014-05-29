package com.prezi.spaghetti.ast

/**
 * Created by lptr on 27/05/14.
 */
interface MethodNode extends NamedNode, AstNode {
	NamedNodeSet<TypeParameterNode> getTypeParameters()
	TypeReference getReturnType()
	NamedNodeSet<MethodParameterNode> getParameters()
}
