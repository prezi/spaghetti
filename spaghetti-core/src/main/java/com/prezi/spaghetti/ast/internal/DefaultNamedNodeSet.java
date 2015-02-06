package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.NamedNode;

import java.util.Set;

public class DefaultNamedNodeSet<T extends NamedNode> extends AbstractNodeSet<String, T> implements NamedNodeSetInternal<T> {
	public DefaultNamedNodeSet(String type) {
		super(type);
	}

	public DefaultNamedNodeSet(String type, Set<T> elements) {
		super(type, elements);
	}

	@Override
	protected String key(T node) {
		return node.getName();
	}

}
