package com.prezi.spaghetti.ast

/**
 * Created by lptr on 27/05/14.
 */
interface InterfaceNode extends AnnotatedNode, DocumentedNode, ReferableTypeNode, MethodContainer<InterfaceMethodNode> {
	NamedNodeSet<TypeParameterNode> getTypeParameters()
	Set<InterfaceReference> getSuperInterfaces()
}
