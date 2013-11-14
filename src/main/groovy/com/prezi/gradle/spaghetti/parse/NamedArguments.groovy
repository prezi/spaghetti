package com.prezi.gradle.spaghetti.parse

/**
 * Created by lptr on 14/11/13.
 */
class NamedArguments {
	final String name
	final def args

	public NamedArguments(String name, Object[] args)
	{
		this.name = name
		this.args = args
	}

	def methodMissing(String name, def args)
	{
		println "Missing method in argument ${name} with ${args}"
		return new NamedArguments(name, args)
	}

	@Override
	String toString() {
		return "NamedArgs: name=${name}, args=${args}"
	}
}
