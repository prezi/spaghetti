package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.ImportNode;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.ast.QualifiedTypeNode;
import com.prezi.spaghetti.ast.TypeNode;

public class LocalModuleTypeResolver implements TypeResolver {
	private final TypeResolver parent;
	private final ModuleNode module;

	public LocalModuleTypeResolver(TypeResolver parent, ModuleNode module) {
		this.parent = parent;
		this.module = module;
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
			type = module.getExternTypes().get(name);
		}

		// If still not found, try parent
		if (type == null) {
			type = (QualifiedTypeNode) parent.resolveType(context.withName(scopedName));
		}

		return type;
	}
}
