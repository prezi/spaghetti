package com.prezi.spaghetti.ast.parser;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.QualifiedTypeNode;
import com.prezi.spaghetti.ast.TypeNode;

import java.util.Collection;
import java.util.Map;

public class SimpleNamedTypeResolver implements TypeResolver {
	private final TypeResolver parent;
	private final Map<FQName, ? extends QualifiedTypeNode> names;

	public SimpleNamedTypeResolver(TypeResolver parent, Collection<? extends QualifiedTypeNode> nodes) {
		this.parent = parent;
		this.names = Maps.uniqueIndex(nodes, new Function<QualifiedTypeNode, FQName>() {
			@Override
			public FQName apply(QualifiedTypeNode node) {
				return node.getQualifiedName();
			}
		});
	}

	@Override
	public TypeNode resolveType(TypeResolutionContext context) {
		TypeNode type = names.get(context.getName());
		if (type == null) {
			type = parent.resolveType(context);
		}

		return type;
	}
}
