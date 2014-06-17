package com.prezi.spaghetti.ast

interface AstNode {
	List<? extends AstNode> getChildren()
	def <T> T accept(ModuleVisitor<? extends T> visitor)
}
