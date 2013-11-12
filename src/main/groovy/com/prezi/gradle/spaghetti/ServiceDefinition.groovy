package com.prezi.gradle.spaghetti

import org.gradle.api.Named

/**
 * Created by lptr on 12/11/13.
 */
class ServiceDefinition implements Named {
	String name

	Map<String, MethodDefinition> methods = [:]

	ServiceDefinition(String name) {
		this.name = name
	}

	void define(Type returnType, String name, Map<String, Type> parameters)
	{
		methods.put(name, new MethodDefinition(name, returnType, parameters))
	}
}
