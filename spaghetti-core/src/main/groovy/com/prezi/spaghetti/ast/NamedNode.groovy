package com.prezi.spaghetti.ast

public interface NamedNode extends AstNode, Comparable<NamedNode> {
	String getName()
}
