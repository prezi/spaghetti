package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.NamedNode;
import com.prezi.spaghetti.ast.QualifiedNode;
import com.prezi.spaghetti.ast.QualifiedTypeNode;
import com.prezi.spaghetti.ast.TypeNodeReference;

import java.util.Set;

public class NodeSets {
	public static <T extends NamedNode> NamedNodeSetInternal<T> newNamedNodeSet(String type) {
		return new DefaultNamedNodeSet<T>(type);
	}

	public static <T extends NamedNode> NamedNodeSetInternal<T> newNamedNodeSet(String type, Set<T> elements) {
		return new DefaultNamedNodeSet<T>(type, elements);
	}

	public static <T extends QualifiedNode> QualifiedNodeSetInternal<T> newQualifiedNodeSet(String type) {
		return new DefaultQualifiedNodeSet<T>(type);
	}

	public static <T extends QualifiedNode> QualifiedNodeSetInternal<T> newQualifiedNodeSet(String type, Set<T> elements) {
		return new DefaultQualifiedNodeSet<T>(type, elements);
	}

	public static <T extends TypeNodeReference<? extends QualifiedTypeNode>> QualifiedTypeNodeReferenceSetInternal<T> newQualifiedNodeReferenceSet(String type) {
		return new DefaultQualifiedTypeNodeReferenceSet<T>(type);
	}

	public static <T extends TypeNodeReference<? extends QualifiedTypeNode>> QualifiedTypeNodeReferenceSetInternal<T> newQualifiedNodeReferenceSet(String type, Set<T> elements) {
		return new DefaultQualifiedTypeNodeReferenceSet<T>(type, elements);
	}
}
