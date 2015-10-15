package com.prezi.spaghetti.ast;

import java.util.List;

public interface FunctionType extends TypeReference {
	List<TypeReference> getElements();
	List<TypeReference> getParameters();
	TypeReference getReturnType();
}
