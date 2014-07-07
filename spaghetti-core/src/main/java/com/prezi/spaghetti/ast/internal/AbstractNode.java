package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Lists;
import com.prezi.spaghetti.ast.AnnotatedNode;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.DocumentedNode;
import com.prezi.spaghetti.ast.ModuleVisitor;

import java.util.ArrayList;

public abstract class AbstractNode implements AstNode {
	@Override
	public <T> T accept(ModuleVisitor<T> visitor) {
		T result = visitor.beforeVisit(this);
		try {
			result = visitor.aggregateResult(result, acceptInternal(visitor));
		} finally {
			result = visitor.afterVisit(this, result);
		}

		return result;
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		ArrayList<AstNode> children = Lists.newArrayList();
		AbstractNode self = this;
		if (self instanceof AnnotatedNode) {
			children.addAll(((AnnotatedNode) self).getAnnotations());
		}

		if (self instanceof DocumentedNode) {
			children.add(((DocumentedNode) self).getDocumentation());
		}

		return children;
	}

	protected abstract <T> T acceptInternal(ModuleVisitor<? extends T> visitor);

	@Override
	public abstract String toString();
}
