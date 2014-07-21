package com.prezi.spaghetti.ast;

public interface MethodNode extends NamedNode, AnnotatedNode {
	NamedNodeSet<TypeParameterNode> getTypeParameters();
	TypeReference getReturnType();
	NamedNodeSet<MethodParameterNode> getParameters();
}
