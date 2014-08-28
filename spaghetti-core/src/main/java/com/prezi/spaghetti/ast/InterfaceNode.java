package com.prezi.spaghetti.ast;

import java.util.Set;

public interface InterfaceNode extends AnnotatedNode, QualifiedTypeNode, ParametrizedReferableTypeNode, MethodContainer<InterfaceMethodNode> {
	Set<InterfaceReference> getSuperInterfaces();
}
