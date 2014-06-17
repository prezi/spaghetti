package com.prezi.spaghetti.ast

import com.prezi.spaghetti.ast.internal.DefaultDocumentationNode

interface DocumentationNode extends AstNode {
	public static final DocumentationNode NONE = new DefaultDocumentationNode(Collections.emptyList())

	List<String> getDocumentation()
}
