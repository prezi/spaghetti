package com.prezi.spaghetti.ast

interface TypeNodeReference<T extends ReferableTypeNode> extends ArrayedTypeReference {
	T getType()
}
