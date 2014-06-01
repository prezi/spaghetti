package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.TypeNode

/**
 * Created by lptr on 29/05/14.
 */
interface TypeResolver {
	TypeNode resolveType(TypeResolutionContext context)
}
