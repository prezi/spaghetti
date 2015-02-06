package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.QualifiedNode;

import java.util.Set;

public class DefaultQualifiedNodeSet<T extends QualifiedNode> extends AbstractNodeSet<FQName, T> implements QualifiedNodeSetInternal<T> {
	private static NodeSetKeyExtractor<FQName, QualifiedNode> DEFAULT_EXTRACTOR = new NodeSetKeyExtractor<FQName, QualifiedNode>() {
		@Override
		public FQName key(QualifiedNode node) {
			return node.getQualifiedName();
		}
	};

	public DefaultQualifiedNodeSet(String type) {
		super(DEFAULT_EXTRACTOR, type);
	}

	public DefaultQualifiedNodeSet(String type, Set<T> elements) {
		super(DEFAULT_EXTRACTOR, type, elements);
	}

	public DefaultQualifiedNodeSet(NodeSetKeyExtractor<FQName, ? super T> extractor, String type, Set<T> elements) {
		super(extractor, type, elements);
	}
}
