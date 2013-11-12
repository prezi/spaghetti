package com.prezi.gradle.spaghetti

/**
 * Created by lptr on 12/11/13.
 */
class MethodDefinition {
	final String name
	final Type returnType
	final Map<String, Type> parameters

	MethodDefinition(String name, Type returnType, Map<String, Type> parameters)
	{
		this.parameters = parameters
		this.returnType = returnType
		this.name = name
	}
}
