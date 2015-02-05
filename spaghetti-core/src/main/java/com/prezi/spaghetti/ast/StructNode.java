package com.prezi.spaghetti.ast;

import java.util.Set;

public interface StructNode extends AnnotatedNode, QualifiedTypeNode, ParametrizedReferableTypeNode, MethodContainer {
	Set<StructReference> getSuperStructs();
	NamedNodeSet<PropertyNode> getProperties();
}
