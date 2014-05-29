package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.ReferableTypeNode
import com.prezi.spaghetti.ast.TypeNodeReference

/**
 * Created by lptr on 27/05/14.
 */
abstract class AbstractTypeNodeReference<T extends ReferableTypeNode> extends AbstractArrayedTypeReference implements TypeNodeReference<T> {
	final T type

	AbstractTypeNodeReference(T type, int arrayDimensions) {
		super(arrayDimensions)
		this.type = type
	}

	@Override
	String toString() {
		return "%" + type.toString() + "[]" * arrayDimensions
	}
}
