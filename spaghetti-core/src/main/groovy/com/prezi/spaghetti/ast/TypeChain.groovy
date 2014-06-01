package com.prezi.spaghetti.ast

/**
 * Created by lptr on 29/05/14.
 */
interface TypeChain extends ArrayedTypeReference {
	List<TypeReference> getElements()
	List<TypeReference> getParameters()
	TypeReference getReturnType()
}
