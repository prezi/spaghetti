package com.prezi.spaghetti.ast;

public interface StructNode extends AnnotatedNode, QualifiedTypeNode, ParametrizedReferableTypeNode, MethodContainer {
	QualifiedTypeNodeReferenceSet<StructReference> getSuperStructs();
	NamedNodeSet<PropertyNode> getProperties();
}
