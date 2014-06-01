package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.DocumentationNode
import com.prezi.spaghetti.ast.DocumentedNode

/**
 * Created by lptr on 30/05/14.
 */
public interface MutableDocumentedNode extends DocumentedNode {
	void setDocumentation(DocumentationNode node)
}
