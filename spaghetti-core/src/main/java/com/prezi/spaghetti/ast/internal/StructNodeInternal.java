package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.StructNode;
import com.prezi.spaghetti.ast.StructReference;

public interface StructNodeInternal extends StructNode, DocumentedNodeInternal, AnnotatedNodeInternal {
	void setSuperStruct(StructReference superStruct);
}
