package com.prezi.spaghetti.ast

interface AnnotatedNode extends AstNode {
	NamedNodeSet<AnnotationNode> getAnnotations()
}
