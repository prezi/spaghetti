package com.prezi.spaghetti.ast

/**
 * Created by lptr on 27/05/14.
 */
interface TypeNodeReference<T extends ReferableTypeNode> extends ArrayedTypeReference {
	T getType()
}
