package com.prezi.spaghetti.ast;

import java.util.Set;

public interface InterfaceNode extends AnnotatedNode, QualifiedTypeNode, ReferableTypeNode, MethodContainer<InterfaceMethodNode> {
	NamedNodeSet<TypeParameterNode> getTypeParameters();
	Set<InterfaceReference> getSuperInterfaces();
}
