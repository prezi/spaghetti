package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AnnotatedNode
import com.prezi.spaghetti.ast.MethodNode
import com.prezi.spaghetti.ast.TypeReference

public interface MutableMethodNode extends AnnotatedNode, MutableDocumentedNode, MethodNode {
	void setReturnType(TypeReference returnType)
}
