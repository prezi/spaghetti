package com.prezi.spaghetti.ast;

public interface MethodContainer<M extends MethodNode> extends AstNode {
	NamedNodeSet<M> getMethods();
}
