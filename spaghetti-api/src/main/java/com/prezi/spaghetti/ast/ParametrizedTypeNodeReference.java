package com.prezi.spaghetti.ast;

import java.util.List;

public interface ParametrizedTypeNodeReference<T extends ParametrizedReferableTypeNode> extends TypeNodeReference<T> {
	List<TypeReference> getArguments();
}
