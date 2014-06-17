package com.prezi.spaghetti.ast;

import java.util.List;

public interface TypeChain extends ArrayedTypeReference {
	List<TypeReference> getElements();
	List<TypeReference> getParameters();
	TypeReference getReturnType();
}
