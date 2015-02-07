package com.prezi.spaghetti.ast;

import com.prezi.spaghetti.ast.internal.PrimitiveTypeReferenceInternal;

public interface PrimitiveTypeReference extends TypeReference {
	public static final PrimitiveTypeReference BOOL = PrimitiveTypeReferenceInternal.BOOL;
	public static final PrimitiveTypeReference INT = PrimitiveTypeReferenceInternal.INT;
	public static final PrimitiveTypeReference FLOAT = PrimitiveTypeReferenceInternal.FLOAT;
	public static final PrimitiveTypeReference STRING = PrimitiveTypeReferenceInternal.STRING;
	public static final PrimitiveTypeReference ANY = PrimitiveTypeReferenceInternal.ANY;

	PrimitiveType getType();
}
