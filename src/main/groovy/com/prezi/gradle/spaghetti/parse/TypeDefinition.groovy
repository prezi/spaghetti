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

	@Override
	def methodMissing(String name, def args)
	{
		if (((Object[]) args).length > 0 && ((Object[]) args)[0] instanceof NamedArguments) {
			NamedArguments namedArgs = ((Object[]) args)[0] as NamedArguments
			def methodName = namedArgs.name
			def methodArgs = (Object[]) namedArgs.args
			def returnType = context.getType(name)
			Map<String, String> parameters = methodArgs.length > 0 ? ((Map<String, String>) methodArgs[0]) : [:]
			methods.put(methodName, new MethodDefinition(methodName, returnType, parameters))
		} else {
			return super.methodMissing(name, args)
		}
	}

	@Override
	String toString() {
		return "type ${name} {\n\t" + methods.values().collect { "${it.name}(${it.parameters}):${it.returnType}"}.join("\n\t") + "\n}"
	}
}
