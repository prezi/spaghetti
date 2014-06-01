package com.prezi.spaghetti.ast

/**
 * Created by lptr on 30/05/14.
 */
interface AstNode {
	List<? extends AstNode> getChildren()
	def <T> T accept(ModuleVisitor<? extends T> visitor)
}
