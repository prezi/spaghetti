package com.prezi.spaghetti.ast;

public interface StructNode extends AnnotatedNode, QualifiedTypeNode, ParametrizedReferableTypeNode {
	NamedNodeSet<PropertyNode> getProperties();
}
