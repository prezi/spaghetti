package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.VoidTypeReference;

public interface VoidTypeReferenceInternal extends VoidTypeReference, TypeReferenceInternal {
	public static final VoidTypeReferenceInternal VOID = new DefaultVoidTypeReference(Location.INTERNAL, 0);
}
