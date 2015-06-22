package com.prezi.spaghetti.ast;

public interface MethodContainer extends AstNode {
	NamedNodeSet<MethodNode> getMethods();
}
