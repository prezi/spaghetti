package com.prezi.spaghetti.ast;

public interface TypeNamePairNode<R extends TypeReference> extends NamedNode {
	R getType();
}
