package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.ReferableTypeNode;
import com.prezi.spaghetti.ast.TypeNodeReference;

public interface TypeNodeReferenceInternal<T extends ReferableTypeNode> extends TypeNodeReference<T>, TypeReferenceInternal {
	T getType();
}
