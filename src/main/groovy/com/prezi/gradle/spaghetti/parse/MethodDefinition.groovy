package com.prezi.gradle.spaghetti.parse

/**
 * Created by lptr on 12/11/13.
 */
class MethodDefinition {
	final String name
	final TypeDefinition returnType
	final Map<String, String> parameters

	MethodDefinition(String name, TypeDefinition returnType, Map<String, String> parameters)
	{
		this.parameters = parameters
		this.returnType = returnType
		this.name = name
	}
}
