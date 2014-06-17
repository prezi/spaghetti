package com.prezi.spaghetti.ast;

public interface AnnotatedNode extends AstNode {
	NamedNodeSet<AnnotationNode> getAnnotations();
}
