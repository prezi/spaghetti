package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.TypeNode

/**
 * Created by lptr on 27/05/14.
 */
abstract class AbstractTypeNode extends AbstractNamedNode implements TypeNode {
	final FQName qualifiedName

	AbstractTypeNode(FQName qualifiedName) {
		super(qualifiedName.localName)
		this.qualifiedName = qualifiedName
	}

	@Override
	String toString() {
		return qualifiedName.toString()
	}
}
