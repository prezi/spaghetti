package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.TypeNode

/**
 * Created by lptr on 29/05/14.
 */
class ModuleTypeResolver implements TypeResolver {
	protected final TypeResolver parent
	protected final ModuleNode module

	ModuleTypeResolver(TypeResolver parent, ModuleNode module) {
		this.parent = parent
		this.module = module
	}

	@Override
	TypeNode resolveType(TypeResolutionContext context) {
		def name = context.name
		def type = module.types.get(name)
		if (!type) {
			type = parent.resolveType(context)
		}
		return type
	}
}
