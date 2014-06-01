package com.prezi.spaghetti.ast

import com.prezi.spaghetti.ast.internal.DefaultDocumentationNode

/**
 * Created by lptr on 30/05/14.
 */
interface DocumentationNode extends AstNode {
	public static final DocumentationNode NONE = new DefaultDocumentationNode(Collections.emptyList())

	List<String> getDocumentation()
}
