package com.prezi.spaghetti.ast;

public interface EnumValueNode extends NamedNode, AnnotatedNode, DocumentedNode {
	Integer getValue();
}
