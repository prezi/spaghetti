package com.prezi.spaghetti.ast;

public interface AstNode {
	Iterable<? extends AstNode> getChildren();
	<T> T accept(ModuleVisitor<T> visitor);
	Location getLocation();
}
