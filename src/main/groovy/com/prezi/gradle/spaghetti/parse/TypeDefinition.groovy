package com.prezi.gradle.spaghetti.parse

import com.prezi.gradle.spaghetti.NamedParser
import org.gradle.api.Named

/**
 * Created by lptr on 12/11/13.
 */
class TypeDefinition extends NamedParser {
	Map<String, MethodDefinition> methods = [:]

	TypeDefinition(String name, ParserContext context) {
		super(name, context)
	}

	void define(NamedArguments namedArgs)
	{
		def methodName = namedArgs.name
		def methodArgs = (Object[]) namedArgs.args
		Map<String, String> parameters = methodArgs.length > 0 ? ((Map<String, String>) methodArgs[0]) : [:]
		methods.put(methodName, new MethodDefinition(methodName, null, parameters))
	}

	@Override
	String toString() {
		return "type ${name} {\n\t" + methods.values().collect { "${it.name}(${it.parameters})"}.join("\n\t") + "\n}"
	}
}
