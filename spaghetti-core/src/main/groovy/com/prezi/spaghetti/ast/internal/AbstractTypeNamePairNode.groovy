package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AstNode
import com.prezi.spaghetti.ast.TypeNamePairNode
import com.prezi.spaghetti.ast.TypeReference

/**
 * Created by lptr on 29/05/14.
 */
abstract class AbstractTypeNamePairNode<R extends TypeReference> extends AbstractNamedNode implements TypeNamePairNode<R> {
	final R type

	AbstractTypeNamePairNode(String name, R type) {
		super(name)
		this.type = type
	}

	@Override
	List<? extends AstNode> getChildren() {
		return super.children + [type]
	}
}
