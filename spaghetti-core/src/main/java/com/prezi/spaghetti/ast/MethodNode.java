package com.prezi.spaghetti.ast;

import java.util.Map;

public interface MethodNode extends NamedNode, AnnotatedNode, DocumentedNode {
	NamedNodeSet<TypeParameterNode> getTypeParameters();
	TypeReference getReturnType();
	NamedNodeSet<MethodParameterNode> getParameters();
	MethodNode resolveWithTypeParameters(Map<TypeParameterNode, TypeReference> bindings);
}
