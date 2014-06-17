package com.prezi.spaghetti.ast.parser;

import com.prezi.spaghetti.ast.TypeNode;

public interface TypeResolver {
	TypeNode resolveType(TypeResolutionContext context);
}
