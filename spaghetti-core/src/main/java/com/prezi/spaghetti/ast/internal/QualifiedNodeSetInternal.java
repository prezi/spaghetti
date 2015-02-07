package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.QualifiedNode;
import com.prezi.spaghetti.ast.QualifiedNodeSet;

public interface QualifiedNodeSetInternal<T extends QualifiedNode> extends QualifiedNodeSet<T>, NodeSetInternal<FQName, T> {
}
