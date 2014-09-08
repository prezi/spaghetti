package com.prezi.spaghetti.ast;

import com.prezi.spaghetti.ast.internal.DefaultNamedNodeSet;
import com.prezi.spaghetti.ast.internal.DefaultQualifiedNodeSet;

import java.util.Set;

public class NodeSets {
	public static <T extends NamedNode> NamedNodeSet<T> newNamedNodeSet(String type) {
		return new DefaultNamedNodeSet<T>(type);
	}

	public static <T extends NamedNode> NamedNodeSet<T> newNamedNodeSet(String type, Set<T> elements) {
		return new DefaultNamedNodeSet<T>(type, elements);
	}

	public static <T extends QualifiedNode> QualifiedNodeSet<T> newQualifiedNodeSet(String type) {
		return new DefaultQualifiedNodeSet<T>(type);
	}

	public static <T extends QualifiedNode> QualifiedNodeSet<T> newQualifiedNodeSet(String type, Set<T> elements) {
		return new DefaultQualifiedNodeSet<T>(type, elements);
	}
}
