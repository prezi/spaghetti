package com.prezi.spaghetti.ast

/**
 * Created by lptr on 29/05/14.
 */
interface TypeNamePairNode<R extends TypeReference> extends NamedNode, AstNode {
	R getType()
}
