package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.DocumentedNode;

public interface DocumentedNodeInternal extends DocumentedNode {
	void setDocumentation(DocumentationNode node);
}
