package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.NamedNode;
import com.prezi.spaghetti.ast.QualifiedNode;

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
}
