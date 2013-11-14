package com.prezi.gradle.spaghetti.parse

/**
 * Created by lptr on 14/11/13.
 */
class Parser {

	protected final ParserContext context

	Parser(ParserContext context) {
		this.context = context
	}

	def methodMissing(String name, def args)
	{
		println "Missing method ${name} with ${args} / " + args.collect { it.class }.join(", ")
		return new NamedArguments(name, args)
	}

	def propertyMissing(String name)
	{
		return context.getType(name)
	}
}
