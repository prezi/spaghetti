package com.prezi.spaghetti.ast;

public interface ConstEntryNode extends NamedNode, AnnotatedNode, TypeNamePairNode<PrimitiveTypeReference> {
	Object getValue();
}
