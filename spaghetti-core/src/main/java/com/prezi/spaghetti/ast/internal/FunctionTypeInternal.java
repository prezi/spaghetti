package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.FunctionType;

import java.util.List;

public interface FunctionTypeInternal extends FunctionType, TypeReferenceInternal, AstNodeInternal {
	List<TypeReferenceInternal> getElementsInternal();
}
