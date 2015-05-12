package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.TypeReference;

public interface TypeReferenceInternal extends TypeReference, AstNodeInternal {
	TypeReferenceInternal withAdditionalArrayDimensions(int extraDimensions);
}
