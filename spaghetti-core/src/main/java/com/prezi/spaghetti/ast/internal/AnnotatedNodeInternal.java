package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.AnnotatedNode;
import com.prezi.spaghetti.ast.AnnotationNode;

public interface AnnotatedNodeInternal extends AnnotatedNode, AstNodeInternal {
	@Override
	NamedNodeSetInternal<AnnotationNode> getAnnotations();
}
