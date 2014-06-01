package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.NamedNode;
import com.prezi.spaghetti.ast.NamedNodeSet;

import java.util.Map;
import java.util.Set;

public class DefaultNamedNodeSet<T extends NamedNode> extends AbstractNodeSet<String, T> implements NamedNodeSet<T> {
	public DefaultNamedNodeSet(String type)
	{
		super(type);
	}

	public DefaultNamedNodeSet(String type, Set<T> elements)
	{
		super(type, elements);
	}

	public DefaultNamedNodeSet(String type, Map<String, T> delegate)
	{
		super(type, delegate);
	}

	@Override
	protected String key(T node)
	{
		return node.getName();
	}

}
