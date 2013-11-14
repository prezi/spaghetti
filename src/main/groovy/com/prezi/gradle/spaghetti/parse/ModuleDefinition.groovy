package com.prezi.gradle.spaghetti.parse

import com.prezi.gradle.spaghetti.NamedParser

/**
 * Created by lptr on 12/11/13.
 */
class ModuleDefinition extends NamedParser {
	String namespace = ""

	ModuleDefinition(String name, ParserContext context)
	{
		super(name, context)
	}

	void namespace(String namespace)
	{
		this.namespace = namespace
	}

	void type(NamedArguments namedArgs)
	{
		String name = namedArgs.name
		Closure cl = ((Object[]) namedArgs.args)[0] as Closure
		def typeDef = new TypeDefinition(name, context)
		context.registerType(typeDef)

		cl.delegate = typeDef
		cl.resolveStrategy = Closure.DELEGATE_FIRST
		cl.run()
	}

	@Override
	String toString() {
		return "module ${name} ->\n\t" + context.types.values().join("\n\t");
	}
}
