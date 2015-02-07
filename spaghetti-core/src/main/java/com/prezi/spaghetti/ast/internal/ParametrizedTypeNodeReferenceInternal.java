package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.ParametrizedReferableTypeNode;
import com.prezi.spaghetti.ast.ParametrizedTypeNodeReference;

import java.util.List;

public interface ParametrizedTypeNodeReferenceInternal<T extends ParametrizedReferableTypeNode> extends ParametrizedTypeNodeReference<T>, TypeNodeReferenceInternal<T> {
	List<TypeReferenceInternal> getArgumentsInternal();
}
