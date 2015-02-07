package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.NodeSet;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.Serializable;
import java.util.Collection;

public interface NodeSetInternal<K extends Serializable, N extends AstNode> extends NodeSet<K, N> {

	void add(N value, ParserRuleContext ctx);

	void add(N value, TerminalNode terminal);

	void add(N value, Token token);

	N remove(K key);

	boolean addInternal(N t);

	boolean addAllInternal(Collection<? extends N> c);

	boolean removeInternal(Object key);

	boolean removeAllInternal(Collection<?> c);

	boolean retainAllInternal(Collection<?> c);

	void clearInternal();
}
