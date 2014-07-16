package com.prezi.spaghetti.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

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

	void add(N value, ParserRuleContext ctx);

	void add(N value, TerminalNode terminal);

	void add(N value, Token token);

	@Deprecated
	@Override
	boolean remove(Object key);

	@Deprecated
	@Override
	boolean removeAll(Collection<?> c);

	@Deprecated
	@Override
	boolean retainAll(Collection<?> c);

	N remove(K key);
}
