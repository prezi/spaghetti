package com.prezi.spaghetti.ast

interface TypeNamePairNode<R extends TypeReference> extends NamedNode, AstNode {
	R getType()
}
