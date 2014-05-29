package com.prezi.spaghetti.ast

/**
 * Created by lptr on 29/05/14.
 */
interface ConstNode extends AnnotatedNode, DocumentedNode, TypeNode {
	NamedNodeSet<ConstEntryNode> getEntries()
}
