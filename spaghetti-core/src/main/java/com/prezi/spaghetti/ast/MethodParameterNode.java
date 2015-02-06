package com.prezi.spaghetti.ast;

import java.util.Map;

public interface MethodParameterNode extends AnnotatedNode, NamedNode, TypeNamePairNode<TypeReference> {
	boolean isOptional();
	MethodParameterNode resolveWithTypeParameters(Map<TypeParameterNode, TypeReference> bindings);
}
