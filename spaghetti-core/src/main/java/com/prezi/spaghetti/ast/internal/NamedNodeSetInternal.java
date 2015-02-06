package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.NamedNode;
import com.prezi.spaghetti.ast.NamedNodeSet;

public interface NamedNodeSetInternal<T extends NamedNode> extends NamedNodeSet<T>, NodeSetInternal<String, T> {
}
