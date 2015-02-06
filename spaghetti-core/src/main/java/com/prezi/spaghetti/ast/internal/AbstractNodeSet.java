package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterators;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.internal.parser.InternalAstParserException;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("NullableProblems")
public abstract class AbstractNodeSet<K extends Serializable, N extends AstNode> implements NodeSetInternal<K, N> {
	private final Map<K, N> delegate;
	private final String type;

	public AbstractNodeSet(String type) {
		this(type, new LinkedHashMap<K, N>());
	}

	public AbstractNodeSet(String type, Set<N> elements) {
		this(type);
		addAllInternal(elements);
	}

	protected AbstractNodeSet(String type, Map<K, N> delegate) {
		this.type = type;
		this.delegate = delegate;
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Deprecated
	@Override
	public boolean contains(Object o) {
		return delegate.containsValue(o);
	}

	@Override
	public boolean contains(K key) {
		return delegate.containsKey(key);
	}

	@Override
	public boolean contains(N value) {
		return delegate.containsValue(value);
	}

	@Override
	public N get(K key) {
		return delegate.get(key);
	}

	@Override
	public Iterator<N> iterator() {
		return Iterators.unmodifiableIterator(delegate.values().iterator());
	}

	@Override
	public Object[] toArray() {
		return delegate.values().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return delegate.values().toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return delegate.values().containsAll(c);
	}

	protected abstract K key(N node);

	@Deprecated
	@Override
	public boolean add(N value) {
		throw new UnsupportedOperationException("External modification of the AST is not allowed");
	}

	@Override
	public boolean addInternal(N value) {
		K key = key(value);
		if (delegate.containsKey(key)) {
			throw new InternalAstParserException(duplicateMessage(key), null);
		}
		delegate.put(key, value);
		return true;
	}

	@Override
	public void add(N value, ParserRuleContext ctx) {
		K key = key(value);
		if (delegate.containsKey(key)) {
			throw new InternalAstParserException(ctx, duplicateMessage(key), null);
		}
		delegate.put(key, value);
	}

	@Override
	public void add(N value, TerminalNode terminal) {
		add(value, terminal.getSymbol());
	}

	@Override
	public void add(N value, Token token) {
		K key = key(value);
		if (delegate.containsKey(key)) {
			throw new InternalAstParserException(token, duplicateMessage(key), null);
		}
		delegate.put(key, value);
	}

	@Deprecated
	@Override
	public boolean addAll(Collection<? extends N> c) {
		throw new UnsupportedOperationException("External modification of the AST is not allowed");
	}

	@Override
	public boolean addAllInternal(Collection<? extends N> c) {
		for (N item : c) {
			addInternal(item);
		}
		return true;
	}

	@Deprecated
	@Override
	public boolean remove(Object key) {
		throw new UnsupportedOperationException("External modification of the AST is not allowed");
	}

	@Override
	public boolean removeInternal(Object key) {
		return delegate.remove(key) != null;
	}

	@Override
	public N remove(K key) {
		return delegate.remove(key);
	}

	@Deprecated
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("External modification of the AST is not allowed");
	}

	@Override
	public boolean removeAllInternal(Collection<?> c) {
		return delegate.values().removeAll(c);
	}

	@Deprecated
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("External modification of the AST is not allowed");
	}

	@Override
	public boolean retainAllInternal(Collection<?> c) {
		return delegate.values().retainAll(c);
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("External modification of the AST is not allowed");
	}

	@Override
	public void clearInternal() {
		delegate.clear();
	}

	private String duplicateMessage(final Object name) {
		return "A(n) " + type + " with the same name already exists: " + name;
	}
}
