package com.prezi.spaghetti.ast;

public interface ConstNode extends AnnotatedNode, QualifiedTypeNode {
	NamedNodeSet<ConstEntryNode> getEntries();
}
