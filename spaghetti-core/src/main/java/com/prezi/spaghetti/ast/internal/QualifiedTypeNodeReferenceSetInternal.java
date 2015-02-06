package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.QualifiedTypeNode;
import com.prezi.spaghetti.ast.QualifiedTypeNodeReferenceSet;
import com.prezi.spaghetti.ast.TypeNodeReference;

public interface QualifiedTypeNodeReferenceSetInternal<T extends TypeNodeReference<? extends QualifiedTypeNode>> extends QualifiedTypeNodeReferenceSet<T>, NodeSetInternal<FQName, T> {
}
