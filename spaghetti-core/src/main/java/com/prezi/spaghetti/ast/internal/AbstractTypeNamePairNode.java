package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.TypeNamePairNode;
import com.prezi.spaghetti.ast.TypeReference;

import java.util.Collections;

public abstract class AbstractTypeNamePairNode<R extends TypeReference> extends AbstractNamedNode implements TypeNamePairNode<R> {
	private final R type;

	public AbstractTypeNamePairNode(Location location, String name, R type) {
		super(location, name);
		this.type = type;
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), Collections.singleton(type));
	}

	@Override
	public final R getType() {
		return type;
	}
}
