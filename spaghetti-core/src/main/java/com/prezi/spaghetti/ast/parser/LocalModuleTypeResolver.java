package com.prezi.spaghetti.ast.parser;

import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.ImportNode;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.ast.QualifiedTypeNode;
import com.prezi.spaghetti.ast.TypeNode;

public class LocalModuleTypeResolver extends ModuleTypeResolver {
	public LocalModuleTypeResolver(TypeResolver parent, ModuleNode module) {
		super(parent, module);
	}

	@Override
	public TypeNode resolveType(TypeResolutionContext context) {
		FQName name = context.getName();

		// Resolve local module types
		ImportNode importDecl = module.getImports().get(name);
		FQName scopedName = importDecl != null ? importDecl.getQualifiedName() : FQName.qualifyLocalName(module.getName(), name);
		QualifiedTypeNode type = module.getTypes().get(scopedName);

		// If not found, try to resolve as locally defined extern
		if (type == null) {
			type = module.getExterns().get(name);
		}

		// If still not found, try parent
		if (type == null) {
			type = ((QualifiedTypeNode) (super.resolveType(context.withName(scopedName))));
		}

		return ((TypeNode) (type));
	}
}
