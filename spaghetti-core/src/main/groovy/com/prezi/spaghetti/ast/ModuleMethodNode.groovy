package com.prezi.spaghetti.ast

/**
 * Created by lptr on 27/05/14.
 */
interface ModuleMethodNode extends AnnotatedNode, DocumentedNode, MethodNode {
	ModuleMethodType getType()
}
