package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.ParametrizedReferableTypeNode;
import com.prezi.spaghetti.ast.ParametrizedTypeNodeReference;
import com.prezi.spaghetti.ast.TypeReference;

import java.util.List;

public interface ParametrizedTypeNodeReferenceInternal<T extends ParametrizedReferableTypeNode> extends ParametrizedTypeNodeReference<T> {
	List<TypeReference> getArgumentsInternal();
}
