package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.TypeNode

class MissingTypeResolver implements TypeResolver {
	public static final TypeResolver INSTANCE = new MissingTypeResolver()

	private MissingTypeResolver() {}

	@Override
	TypeNode resolveType(TypeResolutionContext context) {
		context.throwError()
		return null
	}
}
