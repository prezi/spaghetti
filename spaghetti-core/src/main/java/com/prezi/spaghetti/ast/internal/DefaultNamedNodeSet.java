package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.NamedNode;

import java.util.Set;

public class DefaultNamedNodeSet<T extends NamedNode> extends AbstractNodeSet<String, T> implements NamedNodeSetInternal<T> {
	private static NodeSetKeyExtractor<String, NamedNode> DEFAULT_EXTRACTOR = new NodeSetKeyExtractor<String, NamedNode>() {
		@Override
		public String key(NamedNode node) {
			return node.getName();
		}
	};

	public DefaultNamedNodeSet(String type) {
		super(DEFAULT_EXTRACTOR, type);
	}

	public DefaultNamedNodeSet(String type, Set<T> elements) {
		super(DEFAULT_EXTRACTOR, type, elements);
	}

	public DefaultNamedNodeSet(NodeSetKeyExtractor<String, ? super T> extractor, String type, Set<T> elements) {
		super(extractor, type, elements);
	}
}
