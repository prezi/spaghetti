package com.prezi.spaghetti.ast;

public interface ParametrizedReferableTypeNode extends ReferableTypeNode {
	NamedNodeSet<TypeParameterNode> getTypeParameters();
}
