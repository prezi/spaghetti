package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.TypeNode

class SimpleNamedTypeResolver implements TypeResolver {

	private final TypeResolver parent
	private final Map<FQName, ? extends TypeNode> names

	SimpleNamedTypeResolver(TypeResolver parent, Collection<? extends TypeNode> nodes) {
		this.parent = parent
		this.names = nodes.collectEntries { node -> [node.qualifiedName, node] }
	}

	@Override
	TypeNode resolveType(TypeResolutionContext context) {
		def type = names.get(context.name)
		if (!type) {
			type = parent.resolveType(context)
		}
		return type
	}
}
