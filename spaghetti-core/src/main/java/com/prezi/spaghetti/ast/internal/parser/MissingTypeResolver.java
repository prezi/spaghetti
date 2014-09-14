package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.TypeNode;

public class MissingTypeResolver implements TypeResolver {
	public static final TypeResolver INSTANCE = new MissingTypeResolver();

	private MissingTypeResolver() {
	}

	@Override
	public TypeNode resolveType(TypeResolutionContext context) {
		context.throwError();
		return null;
	}
}
