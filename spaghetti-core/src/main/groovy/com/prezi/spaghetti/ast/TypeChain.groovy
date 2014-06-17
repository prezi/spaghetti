package com.prezi.spaghetti.ast

interface TypeChain extends ArrayedTypeReference {
	List<TypeReference> getElements()
	List<TypeReference> getParameters()
	TypeReference getReturnType()
}
