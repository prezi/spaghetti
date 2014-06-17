package com.prezi.spaghetti.ast;

public interface MethodNode extends NamedNode {
	NamedNodeSet<TypeParameterNode> getTypeParameters();
	TypeReference getReturnType();
	NamedNodeSet<MethodParameterNode> getParameters();
}
