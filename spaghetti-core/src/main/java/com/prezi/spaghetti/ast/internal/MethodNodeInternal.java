package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.MethodNode;
import com.prezi.spaghetti.ast.MethodParameterNode;
import com.prezi.spaghetti.ast.TypeParameterNode;
import com.prezi.spaghetti.ast.TypeReference;

public interface MethodNodeInternal extends AnnotatedNodeInternal, MethodNode {
	@Override
	NamedNodeSetInternal<TypeParameterNode> getTypeParameters();
	@Override
	NamedNodeSetInternal<MethodParameterNode> getParameters();
	void setReturnType(TypeReference returnType);
}
