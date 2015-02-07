package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.TypeReference;

public interface TypeReferenceInternal extends TypeReference {
	TypeReferenceInternal withAdditionalArrayDimensions(int extraDimensions);
}
