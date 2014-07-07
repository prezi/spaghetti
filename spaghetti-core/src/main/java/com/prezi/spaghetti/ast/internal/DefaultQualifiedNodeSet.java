package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.QualifiedNode;
import com.prezi.spaghetti.ast.QualifiedNodeSet;

import java.util.Map;

public class DefaultQualifiedNodeSet<T extends QualifiedNode> extends AbstractNodeSet<FQName, T> implements QualifiedNodeSet<T> {
	public DefaultQualifiedNodeSet(String type)
	{
		super(type);
	}

	public DefaultQualifiedNodeSet(String type, Map<FQName, T> delegate)
	{
		super(type, delegate);
	}

	@Override
	protected FQName key(T node)
	{
		return node.getQualifiedName();
	}

}
