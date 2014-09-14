package com.prezi.spaghetti.ast;

import com.prezi.spaghetti.ast.internal.DefaultVoidTypeReference;

public interface VoidTypeReference extends TypeReference {
	public static final VoidTypeReference VOID = new DefaultVoidTypeReference();
}
