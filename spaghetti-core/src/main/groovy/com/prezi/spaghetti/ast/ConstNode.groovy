package com.prezi.spaghetti.ast

interface ConstNode extends AnnotatedNode, DocumentedNode, TypeNode {
	NamedNodeSet<ConstEntryNode> getEntries()
}
