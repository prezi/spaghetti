package com.prezi.spaghetti.ast;

import com.prezi.spaghetti.ast.internal.VoidTypeReferenceInternal;

public interface VoidTypeReference extends TypeReference {
	public static final VoidTypeReference VOID = VoidTypeReferenceInternal.VOID;
}
