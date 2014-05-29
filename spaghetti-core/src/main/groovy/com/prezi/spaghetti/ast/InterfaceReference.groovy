package com.prezi.spaghetti.ast

/**
 * Created by lptr on 29/05/14.
 */
interface InterfaceReference extends TypeNodeReference<InterfaceNode> {
	List<TypeReference> getArguments()
}
