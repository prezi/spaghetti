package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.TypeNode

/**
 * Created by lptr on 29/05/14.
 */
class LocalModuleTypeResolver extends ModuleTypeResolver {
	LocalModuleTypeResolver(TypeResolver parent, ModuleNode module) {
		super(parent, module)
	}

	@Override
	TypeNode resolveType(TypeResolutionContext context) {
		def name = context.name

		// Resolve local module types
		FQName scopedName = module.imports.get(name)?.qualifiedName ?: FQName.qualifyLocalName(module.name, name)
		def type = module.types.get(scopedName)

		// If not found, try to resolve as locally defined extern
		if (!type) {
			type = module.externs.get(name)
		}

		// If still not found, try parent
		if (!type) {
			type = super.resolveType(context.withName(scopedName))
		}
		return type
	}
}
