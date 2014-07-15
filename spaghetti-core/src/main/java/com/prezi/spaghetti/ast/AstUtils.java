package com.prezi.spaghetti.ast;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class AstUtils {
	public static Iterable<String> getNames(Iterable<? extends NamedNode> nodes) {
		return Iterables.transform(nodes, new Function<NamedNode, String>() {
			@Override
			public String apply(NamedNode node) {
				return node.getName();
			}
		});
	}

	public static <T> Iterable<T> acceptAll(Iterable<? extends AstNode> nodes, final ModuleVisitor<T> visitor) {
		return Iterables.transform(nodes, new Function<AstNode, T>() {
			@Override
			public T apply(AstNode node) {
				return node.accept(visitor);
			}
		});
	}
}
