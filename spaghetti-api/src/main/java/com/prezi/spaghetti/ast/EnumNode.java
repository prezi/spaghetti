package com.prezi.spaghetti.ast;

public interface EnumNode extends AnnotatedNode, QualifiedTypeNode, ReferableTypeNode {
	NamedNodeSet<EnumValueNode> getValues();
}
