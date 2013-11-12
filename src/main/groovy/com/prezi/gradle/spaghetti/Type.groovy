package com.prezi.gradle.spaghetti

/**
 * Created by lptr on 12/11/13.
 */
class Type {
	final String name

	Type(String name)
	{
		this.name = name;
	}

	@Override
	String toString()
	{
		return name
	}
}
