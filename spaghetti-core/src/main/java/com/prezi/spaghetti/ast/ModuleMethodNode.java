package com.prezi.spaghetti.ast;

public interface ModuleMethodNode extends AnnotatedNode, MethodNode {
	ModuleMethodType getType();
}
