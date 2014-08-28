package com.prezi.spaghetti.ast;

public interface StructNode extends AnnotatedNode, QualifiedTypeNode, ParametrizedReferableTypeNode, MethodContainer<TypeMethodNode> {
	NamedNodeSet<PropertyNode> getProperties();
}
