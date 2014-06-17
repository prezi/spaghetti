package com.prezi.spaghetti.ast

interface EnumNode extends AnnotatedNode, DocumentedNode, ReferableTypeNode {
	NamedNodeSet<EnumValueNode> getValues()
}
