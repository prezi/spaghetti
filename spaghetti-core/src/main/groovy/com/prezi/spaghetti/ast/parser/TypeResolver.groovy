package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.TypeNode

interface TypeResolver {
	TypeNode resolveType(TypeResolutionContext context)
}
