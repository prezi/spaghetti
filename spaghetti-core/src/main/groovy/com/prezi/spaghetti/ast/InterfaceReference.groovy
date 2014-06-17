package com.prezi.spaghetti.ast

interface InterfaceReference extends TypeNodeReference<InterfaceNode> {
	List<TypeReference> getArguments()
}
