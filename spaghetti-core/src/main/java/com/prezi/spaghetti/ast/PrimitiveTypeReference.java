package com.prezi.spaghetti.ast;

import com.prezi.spaghetti.ast.internal.DefaultPrimitiveTypeReference;

public interface PrimitiveTypeReference extends TypeReference {
	public static final PrimitiveTypeReference BOOL = new DefaultPrimitiveTypeReference(Location.INTERNAL, PrimitiveType.BOOL, 0);
	public static final PrimitiveTypeReference INT = new DefaultPrimitiveTypeReference(Location.INTERNAL, PrimitiveType.INT, 0);
	public static final PrimitiveTypeReference FLOAT = new DefaultPrimitiveTypeReference(Location.INTERNAL, PrimitiveType.FLOAT, 0);
	public static final PrimitiveTypeReference STRING = new DefaultPrimitiveTypeReference(Location.INTERNAL, PrimitiveType.STRING, 0);
	public static final PrimitiveTypeReference ANY = new DefaultPrimitiveTypeReference(Location.INTERNAL, PrimitiveType.ANY, 0);

	PrimitiveType getType();
}
