package com.prezi.spaghetti.ast;

import com.prezi.spaghetti.ast.internal.DefaultDocumentationNode;

import java.util.Collections;
import java.util.List;

public interface DocumentationNode extends AstNode {
	public static final DocumentationNode NONE = new DefaultDocumentationNode(Collections.<String> emptyList());

	List<String> getDocumentation();
}
