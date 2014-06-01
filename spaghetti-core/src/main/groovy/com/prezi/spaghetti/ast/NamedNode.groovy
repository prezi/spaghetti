package com.prezi.spaghetti.ast

/**
 * Created by lptr on 27/05/14.
 */
public interface NamedNode extends AstNode, Comparable<NamedNode> {
	String getName()
}
