package com.prezi.spaghetti.ast

/**
 * Created by lptr on 30/05/14.
 */
interface AnnotatedNode extends AstNode {
	NamedNodeSet<AnnotationNode> getAnnotations()
}
