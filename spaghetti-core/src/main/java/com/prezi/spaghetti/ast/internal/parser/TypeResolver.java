package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.TypeNode;

public interface TypeResolver {
	TypeNode resolveType(TypeResolutionContext context);
}
