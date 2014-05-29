package com.prezi.spaghetti.ast

/**
 * Created by lptr on 30/05/14.
 */
public interface MethodContainer<M extends MethodNode> extends AstNode {
	NamedNodeSet<M> getMethods()
}
