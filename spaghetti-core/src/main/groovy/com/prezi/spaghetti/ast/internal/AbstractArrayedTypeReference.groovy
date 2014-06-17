package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.ArrayedTypeReference

abstract class AbstractArrayedTypeReference extends AbstractNode implements ArrayedTypeReference {
	final int arrayDimensions

	AbstractArrayedTypeReference(int arrayDimensions) {
		this.arrayDimensions = arrayDimensions
	}
}
