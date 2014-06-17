package com.prezi.spaghetti.ast

interface ModuleMethodNode extends AnnotatedNode, DocumentedNode, MethodNode {
	ModuleMethodType getType()
}
