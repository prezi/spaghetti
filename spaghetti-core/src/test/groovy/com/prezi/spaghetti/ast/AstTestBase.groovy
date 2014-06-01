package com.prezi.spaghetti.ast

import com.prezi.spaghetti.ast.parser.TypeResolutionContext
import com.prezi.spaghetti.ast.parser.TypeResolver
import spock.lang.Specification

/**
 * Created by lptr on 31/05/14.
 */
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
