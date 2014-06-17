package com.prezi.spaghetti.ast;

public interface StructNode extends AnnotatedNode, QualifiedTypeNode, ReferableTypeNode {
	NamedNodeSet<TypeParameterNode> getTypeParameters();
	NamedNodeSet<PropertyNode> getProperties();
}
