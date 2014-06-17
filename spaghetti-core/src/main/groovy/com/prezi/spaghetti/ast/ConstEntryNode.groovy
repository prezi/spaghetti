package com.prezi.spaghetti.ast
interface ConstEntryNode extends AnnotatedNode, DocumentedNode, TypeNamePairNode<PrimitiveTypeReference> {
	Object getValue()
}
