package com.prezi.spaghetti.ast;

public interface StructNode extends AnnotatedNode, QualifiedTypeNode, ParametrizedReferableTypeNode, MethodContainer {
	StructReference getSuperStruct();
	NamedNodeSet<PropertyNode> getProperties();
}
