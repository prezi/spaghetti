package com.prezi.spaghetti.ast

import com.prezi.spaghetti.ast.internal.DefaultPrimitiveTypeReference

interface PrimitiveTypeReference extends ArrayedTypeReference {
	public static final PrimitiveTypeReference BOOL = new DefaultPrimitiveTypeReference(PrimitiveType.BOOL, 0)
	public static final PrimitiveTypeReference INT = new DefaultPrimitiveTypeReference(PrimitiveType.INT, 0)
	public static final PrimitiveTypeReference FLOAT = new DefaultPrimitiveTypeReference(PrimitiveType.FLOAT, 0)
	public static final PrimitiveTypeReference STRING = new DefaultPrimitiveTypeReference(PrimitiveType.STRING, 0)
	public static final PrimitiveTypeReference ANY = new DefaultPrimitiveTypeReference(PrimitiveType.ANY, 0)

	PrimitiveType getType()
}
