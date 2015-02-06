package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.AstNode;

import java.io.Serializable;

public interface NodeSetKeyExtractor<K extends Serializable, N extends AstNode> {
	K key(N node);
}
