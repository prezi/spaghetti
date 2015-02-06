package com.prezi.spaghetti.ast;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

@SuppressWarnings("NullableProblems")
public interface NodeSet<K extends Serializable, N extends AstNode> extends Set<N> {
	@Deprecated
	@Override
	boolean contains(Object o);

	boolean contains(K key);

	boolean contains(N value);

	N get(K key);

	@Deprecated
	@Override
	boolean add(N t);

	@Deprecated
	@Override
	boolean addAll(Collection<? extends N> c);

	@Deprecated
	@Override
	boolean remove(Object key);

	@Deprecated
	@Override
	boolean removeAll(Collection<?> c);

	@Deprecated
	@Override
	boolean retainAll(Collection<?> c);

	@Deprecated
	@Override
	void clear();
}
