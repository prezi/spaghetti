package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.TypeChain;

import java.util.List;

public interface TypeChainInternal extends TypeChain, TypeReferenceInternal {
	List<TypeReferenceInternal> getElementsInternal();
}
