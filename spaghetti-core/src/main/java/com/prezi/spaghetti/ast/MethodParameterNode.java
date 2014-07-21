package com.prezi.spaghetti.ast;

public interface MethodParameterNode extends AnnotatedNode, NamedNode, TypeNamePairNode<TypeReference> {
	boolean isOptional();
	Object getOptionalValue();
}
