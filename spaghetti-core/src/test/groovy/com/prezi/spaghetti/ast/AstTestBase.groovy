package com.prezi.spaghetti.ast

import com.prezi.spaghetti.ast.internal.parser.TypeResolutionContext
import com.prezi.spaghetti.ast.internal.parser.TypeResolver
import spock.lang.Specification

class AstTestBase extends Specification {
	protected TypeResolver mockResolver(Map<String, Closure<TypeNode>> mocker = [:]) {
		def resolver = Mock(TypeResolver)
		resolver.resolveType(_) >> { args ->
			TypeResolutionContext context = args[0]
			def name = context.name
			def typeMocker = mocker.get(name.fullyQualifiedName)
			def type = typeMocker ? typeMocker() : null
			if (type) {
				return type
			}

			throw new IllegalStateException("Ran out of scope while looking for type: ${name}")
		}
		return resolver
	}
}
