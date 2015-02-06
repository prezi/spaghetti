package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.QualifiedTypeNode;
import com.prezi.spaghetti.ast.TypeNodeReference;

import java.util.Set;

public class DefaultQualifiedTypeNodeReferenceSet<T extends TypeNodeReference<? extends QualifiedTypeNode>> extends AbstractNodeSet<FQName, T> implements QualifiedTypeNodeReferenceSetInternal<T> {
	private static NodeSetKeyExtractor<FQName, TypeNodeReference<? extends QualifiedTypeNode>> DEFAULT_EXTRACTOR = new NodeSetKeyExtractor<FQName, TypeNodeReference<? extends QualifiedTypeNode>>() {
		@Override
		public FQName key(TypeNodeReference<? extends QualifiedTypeNode> ref) {
			return ref.getType().getQualifiedName();
		}
	};

	public DefaultQualifiedTypeNodeReferenceSet(String type) {
		super(DEFAULT_EXTRACTOR, type);
	}

	public DefaultQualifiedTypeNodeReferenceSet(String type, Set<T> elements) {
		super(DEFAULT_EXTRACTOR, type, elements);
	}

	public DefaultQualifiedTypeNodeReferenceSet(NodeSetKeyExtractor<FQName, ? super T> extractor, String type, Set<T> elements) {
		super(extractor, type, elements);
	}
}
