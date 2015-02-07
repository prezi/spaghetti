package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.PrimitiveType;
import com.prezi.spaghetti.ast.PrimitiveTypeReference;

public interface PrimitiveTypeReferenceInternal extends PrimitiveTypeReference, TypeReferenceInternal {
	public static final PrimitiveTypeReferenceInternal BOOL = new DefaultPrimitiveTypeReference(Location.INTERNAL, PrimitiveType.BOOL, 0);
	public static final PrimitiveTypeReferenceInternal INT = new DefaultPrimitiveTypeReference(Location.INTERNAL, PrimitiveType.INT, 0);
	public static final PrimitiveTypeReferenceInternal FLOAT = new DefaultPrimitiveTypeReference(Location.INTERNAL, PrimitiveType.FLOAT, 0);
	public static final PrimitiveTypeReferenceInternal STRING = new DefaultPrimitiveTypeReference(Location.INTERNAL, PrimitiveType.STRING, 0);
	public static final PrimitiveTypeReferenceInternal ANY = new DefaultPrimitiveTypeReference(Location.INTERNAL, PrimitiveType.ANY, 0);
}
