package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.DocumentationNode;

import java.util.Collections;

public interface DocumentationNodeInternal extends DocumentationNode, AstNodeInternal {
	DocumentationNode NONE = new DefaultDocumentationNode(DefaultLocation.INTERNAL, Collections.<String> emptyList());
}
