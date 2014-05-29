package com.prezi.spaghetti.ast
/**
 * Created by lptr on 29/05/14.
 */
interface ConstEntryNode extends AnnotatedNode, DocumentedNode, TypeNamePairNode<PrimitiveTypeReference> {
	Object getValue()
}
