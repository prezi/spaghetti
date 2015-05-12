package com.prezi.spaghetti.ast;

public interface TypeNodeReference<T extends ReferableTypeNode> extends TypeReference {
	T getType();
}
