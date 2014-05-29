package com.prezi.spaghetti.ast

/**
 * Created by lptr on 29/05/14.
 */
interface EnumNode extends AnnotatedNode, DocumentedNode, ReferableTypeNode {
	NamedNodeSet<EnumValueNode> getValues()
}
