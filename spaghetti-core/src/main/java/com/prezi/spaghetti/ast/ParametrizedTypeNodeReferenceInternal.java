package com.prezi.spaghetti.ast;

import java.util.List;

public interface ParametrizedTypeNodeReferenceInternal<T extends ParametrizedReferableTypeNode> extends ParametrizedTypeNodeReference<T> {
	List<TypeReference> getArgumentsInternal();
}
